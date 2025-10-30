package mocmien.com.controller.vendor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import mocmien.com.dto.response.revenue.RevenuePoint;
import mocmien.com.dto.response.revenue.RevenueSummaryResponse;
import mocmien.com.dto.response.revenue.RecentOrderItem;
import mocmien.com.service.VendorRevenueService;
import mocmien.com.repository.StoreRepository;
import mocmien.com.security.CustomUserDetails;

@RestController
@RequestMapping("/api/vendor/revenue")
@PreAuthorize("hasRole('VENDOR')")
public class VendorRevenueController {

	private final VendorRevenueService revenueService;
	private final StoreRepository storeRepo;

	public VendorRevenueController(final VendorRevenueService revenueService, final StoreRepository storeRepo) {
		this.revenueService = revenueService;
		this.storeRepo = storeRepo;
	}

    // Lấy vendor userId từ SecurityContext
    private Integer currentVendorId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Chưa đăng nhập");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        throw new IllegalStateException("Principal không hợp lệ");
    }

	private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE;

	// ===== SUMMARY =====
	// GET /api/vendor/revenue/summary?from=2025-01-01&to=2025-10-30
	@GetMapping("/summary")
	public ResponseEntity<RevenueSummaryResponse> summary(@RequestParam String from, @RequestParam String to) {
		Integer vendorUserId = currentVendorId();
		LocalDate f = LocalDate.parse(from, ISO);
		LocalDate t = LocalDate.parse(to, ISO);
		return ResponseEntity.ok(revenueService.summary(vendorUserId, f, t));
	}

	// ===== DAILY CHART =====
	// GET /api/vendor/revenue/daily?from=2025-10-01&to=2025-10-30
	@GetMapping("/daily")
	public ResponseEntity<List<RevenuePoint>> daily(@RequestParam String from, @RequestParam String to) {
		Integer vendorUserId = currentVendorId();
		LocalDate f = LocalDate.parse(from, ISO);
		LocalDate t = LocalDate.parse(to, ISO);
		return ResponseEntity.ok(revenueService.daily(vendorUserId, f, t));
	}

	// ===== MONTHLY CHART =====
	// GET /api/vendor/revenue/monthly?year=2025
	@GetMapping("/monthly")
	public ResponseEntity<List<RevenuePoint>> monthly(@RequestParam int year) {
		Integer vendorUserId = currentVendorId();
		return ResponseEntity.ok(revenueService.monthly(vendorUserId, year));
	}

	// ===== RECENT ORDERS =====
	// GET /api/vendor/revenue/recent?limit=8
	@GetMapping("/recent")
	public ResponseEntity<List<RecentOrderItem>> recent(@RequestParam(defaultValue = "8") int limit) {
		Integer vendorUserId = currentVendorId();
		return ResponseEntity.ok(revenueService.recent(vendorUserId, limit));
	}
}
