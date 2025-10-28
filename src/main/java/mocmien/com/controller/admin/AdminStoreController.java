package mocmien.com.controller.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mocmien.com.dto.response.store.StoreResponse;
import mocmien.com.dto.response.store.StoreStats;
import mocmien.com.entity.Store;
import mocmien.com.service.StoreService;

@RestController
@RequestMapping("/api/admin/store")
public class AdminStoreController {
	
	@Autowired
	private StoreService storeService;
	
	// -----------------------
    // 1. THỐNG KÊ CỬA HÀNG
    // -----------------------
    @GetMapping("/stats")
    public ResponseEntity<StoreStats> getStoreStatistics() {
        StoreStats stats = storeService.getStoreStatistics();
        return ResponseEntity.ok(stats);
    }
    
    // -----------------------
    // 2. TÌM KIẾM & PHÂN TRANG
    // -----------------------
    @GetMapping
    public ResponseEntity<Page<StoreResponse>> searchStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean status) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Store> storePage;
        
        // Tìm kiếm theo điều kiện
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm theo tên cửa hàng
            storePage = storeService.findByStoreNameContainingIgnoreCase(keyword.trim(), pageable);
        } else if (status != null) {
            // Lọc theo trạng thái isActive
            storePage = storeService.findByIsActive(status, pageable);
        } else {
            // Lấy tất cả - cần thêm method này vào StoreService
            // Tạm thời lấy tất cả store active
            storePage = storeService.findAll(pageable);
        }
        
        // Convert sang DTO
        Page<StoreResponse> response = storePage.map(this::convertToResponse);
        
        return ResponseEntity.ok(response);
    }
    
    // -----------------------
    // 3. XEM CHI TIẾT CỬA HÀNG
    // -----------------------
    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreDetail(@PathVariable Integer storeId) {
        Optional<Store> storeOpt = storeService.findById(storeId);
        
        if (storeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy cửa hàng"));
        }
        
        Store store = storeOpt.get();
        StoreResponse response = convertToResponse(store);
        
        return ResponseEntity.ok(response);
    }
    
    // -----------------------
    // 4. KHÓA/MỞ KHÓA CỬA HÀNG
    // -----------------------
    @PatchMapping("/{storeId}/block")
    public ResponseEntity<?> toggleBlockStore(@PathVariable Integer storeId) {
        Optional<Store> storeOpt = storeService.findById(storeId);
        
        if (storeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy cửa hàng"));
        }
        
        storeService.changeBlock(storeId);
        
        return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công"));
    }
    
    // -----------------------
    // 5. XÓA CỬA HÀNG
    // -----------------------
    @DeleteMapping("/{storeId}")
    public ResponseEntity<?> deleteStore(@PathVariable Integer storeId) {
        Optional<Store> storeOpt = storeService.findById(storeId);
        
        if (storeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy cửa hàng"));
        }
        
        storeService.deleteStore(storeId);
        
        return ResponseEntity.ok(Map.of("message", "Xóa cửa hàng thành công"));
    }
    
    // -----------------------
    // HELPER: Convert Entity -> DTO
    // -----------------------
    private StoreResponse convertToResponse(Store store) {
        StoreResponse dto = new StoreResponse();
        dto.setId(store.getId());
        dto.setStoreName(store.getStoreName());
        dto.setVendorName(store.getVendor() != null ? store.getVendor().getUsername() : "N/A");
        dto.setLevelName(store.getLevel() != null ? store.getLevel().toString() : "N/A");
        dto.setPhone(store.getPhone());
        dto.setAddress(store.getAddress());
        dto.setAvatar(store.getAvatar());
        dto.setCover(store.getCover());
        dto.setPoint(store.getPoint());
        dto.setRating(store.getRating());
        dto.seteWallet(store.geteWallet());
        dto.setActive(store.isActive());
        dto.setOpen(store.isOpen());
        dto.setCreateAt(store.getCreateAt());
        dto.setUpdateAt(store.getUpdateAt());
        return dto;
    }
}