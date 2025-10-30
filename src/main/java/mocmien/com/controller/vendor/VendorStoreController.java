package mocmien.com.controller.vendor;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import mocmien.com.dto.request.store.StoreRegisterRequest;
import mocmien.com.dto.request.store.ToggleOpenRequest;
import mocmien.com.dto.response.store.AdminStoreResponse;
import mocmien.com.entity.Level;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.enums.Rank;
import mocmien.com.repository.LevelRepository;
import mocmien.com.service.StoreService;
import mocmien.com.service.UserService;

@RestController
@RequestMapping("/api/vendor/store")
@PreAuthorize("hasRole('VENDOR')")
public class VendorStoreController {

    private final StoreService storeService;
    private final UserService userService;
    private final LevelRepository levelRepository;

    public VendorStoreController(
            StoreService storeService,
            UserService userService,
            LevelRepository levelRepository
    ) {
        this.storeService = storeService;
        this.userService = userService;
        this.levelRepository = levelRepository;
    }

    /** Lấy cửa hàng của vendor đang đăng nhập (không cần vendorId từ FE) */
    @GetMapping("/me")
    public ResponseEntity<?> getMyStore(Authentication authentication) {
        User vendor = resolveCurrentVendor(authentication);
        if (vendor == null) {
            return ResponseEntity.badRequest().body("Vendor không tồn tại");
        }

        List<Store> stores = storeService.findByVendor(vendor);
        if (stores.isEmpty()) {
            // Vendor chưa có shop
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(buildStoreResponse(stores.get(0)));
    }

    /**
     * Vendor đăng ký Shop lần đầu.
     * - level mặc định = Rank.NEW
     * - ví/điểm/rating = 0
     * - isActive=false (chờ duyệt), isOpen theo form nhưng chưa hiển thị nếu chưa active
     */
    
    @PostMapping("/register")
    public ResponseEntity<?> registerStore(@RequestBody StoreRegisterRequest req,
                                           Authentication authentication) {
        User vendor = resolveCurrentVendor(authentication);
        if (vendor == null) {
            return ResponseEntity.badRequest().body("Vendor không tồn tại");
        }

        // Một vendor chỉ được tạo 1 shop
        if (!storeService.findByVendor(vendor).isEmpty()) {
            return ResponseEntity.badRequest().body("Bạn đã có cửa hàng, không thể đăng ký thêm.");
        }

        // Level mặc định Rank.NEW (tạo nếu chưa có)
        Level level = levelRepository.findByName(Rank.NEW)
                .orElseGet(() -> {
                    Level lv = new Level();
                    lv.setName(Rank.NEW);
                    lv.setMinPoint(0);
                    lv.setDiscount(BigDecimal.ZERO);
                    return levelRepository.save(lv);
                });

        // Tạo store
        Store store = new Store();
        store.setVendor(vendor);
        store.setLevel(level);
        store.setStoreName(req.getStoreName());
        // Build address string from structured fields if provided
        String address = req.getAddress();
        if ((address == null || address.isBlank())) {
            StringBuilder sb = new StringBuilder();
            if (req.getLine() != null && !req.getLine().isBlank()) sb.append(req.getLine());
            if (req.getWard() != null && !req.getWard().isBlank()) sb.append(sb.length() > 0 ? ", " : "").append(req.getWard());
            if (req.getDistrict() != null && !req.getDistrict().isBlank()) sb.append(sb.length() > 0 ? ", " : "").append(req.getDistrict());
            if (req.getProvince() != null && !req.getProvince().isBlank()) sb.append(sb.length() > 0 ? ", " : "").append(req.getProvince());
            address = sb.toString();
        }
        store.setAddress(address);
        // Coordinates
        if (req.getLatitude() != null) store.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) store.setLongitude(req.getLongitude());
        store.setAvatar(req.getAvatar());
        store.setCover(req.getCover());
        if (req.getFeatureImages() != null) {
            store.setFeatureImages(new ArrayList<>(req.getFeatureImages()));
        }
        store.setPoint(0);
        store.seteWallet(BigDecimal.ZERO);
        store.setRating(BigDecimal.ZERO);
        store.setActive(false); // chờ duyệt
        store.setOpen(req.getIsOpen() != null && req.getIsOpen());

        // Tạo slug duy nhất cho store
        String slugBase = slugify(req.getStoreName());
        String uniqueSlug = uniqueStoreSlug(slugBase);
        store.setSlug(uniqueSlug);
        Store saved = storeService.save(store);
        return ResponseEntity.ok(buildStoreResponse(saved));
    }

    // =============== Helpers cho Slug ===============
    /** Gen ra slug-url từ tên tiếng Việt, lowerCase, ko dấu, thay space bằng '-' */
    private String slugify(String input) {
        if (input == null) return "";
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase()
                .replaceAll("đ", "d").replaceAll("Đ", "D")
                .replaceAll("[^a-z0-9\\s-]", "") // bỏ các ký tự không mong muốn
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
        return slug;
    }

    /** Đảm bảo slug duy nhất cho store (nếu trùng thì thêm số -- như shop, shop-2...) */
    private String uniqueStoreSlug(String base) {
        String slug = base;
        int i = 1;
        while (storeService.findBySlug(slug).isPresent()) {
            slug = base + "-" + (++i);
        }
        return slug;
    }

    // ================== Helpers ==================

    /** Tìm vendor từ Authentication: ưu tiên lấy userId, fallback bằng username */
    private User resolveCurrentVendor(Authentication authentication) {
        if (authentication == null) return null;

        // 1) Thử lấy userId từ principal (hỗ trợ nhiều kiểu custom)
        Integer uid = extractUserId(authentication);
        if (uid != null) {
            Optional<User> byId = userService.findById(uid);
            if (byId.isPresent()) return byId.get();
        }

        // 2) Fallback: lấy username rồi tra DB
        String username = authentication.getName();
        if (username != null && !"anonymousUser".equalsIgnoreCase(username)) {
            return userService.findByUsername(username).orElse(null);
        }
        return null;
    }

    private Integer extractUserId(Authentication authentication) {
        try {
            Object p = authentication.getPrincipal();
            if (p == null) return null;

            // 1) getUserId()
            try {
                var m = p.getClass().getMethod("getUserId");
                Object v = m.invoke(p);
                if (v instanceof Integer) return (Integer) v;
                if (v instanceof Long)    return Math.toIntExact((Long) v);
            } catch (NoSuchMethodException ignored) {}

            // 2) getId()
            try {
                var m = p.getClass().getMethod("getId");
                Object v = m.invoke(p);
                if (v instanceof Integer) return (Integer) v;
                if (v instanceof Long)    return Math.toIntExact((Long) v);
            } catch (NoSuchMethodException ignored) {}

            // 3) getUser() -> getId()
            try {
                var mUser = p.getClass().getMethod("getUser");
                Object u = mUser.invoke(p);
                if (u != null) {
                    try {
                        var mId = u.getClass().getMethod("getId");
                        Object v = mId.invoke(u);
                        if (v instanceof Integer) return (Integer) v;
                        if (v instanceof Long)    return Math.toIntExact((Long) v);
                    } catch (NoSuchMethodException ignored) {}
                }
            } catch (NoSuchMethodException ignored) {}

        } catch (Exception ignored) { }
        return null;
    }

    private AdminStoreResponse buildStoreResponse(Store s) {
        AdminStoreResponse r = new AdminStoreResponse();
        r.setId(s.getId());
        r.setStoreName(s.getStoreName());
        r.setAddress(s.getAddress());
        r.setAvatar(s.getAvatar());
        r.setCover(s.getCover());
        r.seteWallet(s.geteWallet());
        r.setRating(s.getRating());
        r.setPoint(s.getPoint());
        r.setActive(s.isActive());
        r.setOpen(s.isOpen());

        if (s.getLevel() != null && s.getLevel().getName() != null) {
            r.setLevelName(s.getLevel().getName().name());
            r.setLevelName(s.getLevel().getName().getDisplayName());
        }

        String ownerName = (s.getVendor().getUserProfile() != null
                && s.getVendor().getUserProfile().getFullName() != null)
                ? s.getVendor().getUserProfile().getFullName()
                : s.getVendor().getUsername();
        r.setVendorName(ownerName);
        r.setPhone(s.getVendor().getPhone());
        return r;
    }
    

    
    @PatchMapping("/open")
    public ResponseEntity<?> setOpen(@RequestBody ToggleOpenRequest req, Authentication authentication) {
        User vendor = resolveCurrentVendor(authentication);
        if (vendor == null) {
            return ResponseEntity.badRequest().body("Vendor không tồn tại");
        }

        List<Store> stores = storeService.findByVendor(vendor);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body("Bạn chưa có cửa hàng");
        }
        Store store = stores.get(0); // 1 vendor = 1 shop

        if (!store.isActive()) {
            return ResponseEntity.badRequest().body("Shop chưa được duyệt, không thể mở bán");
        }

        boolean willOpen = Boolean.TRUE.equals(req.getOpen());
        store.setOpen(willOpen);

        Store saved = storeService.save(store);
        return ResponseEntity.ok(buildStoreResponse(saved));
    }
}
