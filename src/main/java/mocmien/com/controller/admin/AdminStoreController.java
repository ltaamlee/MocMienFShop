package mocmien.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import mocmien.com.dto.response.users.UserStats;
import mocmien.com.entity.Store;
import mocmien.com.service.StoreService;
import mocmien.com.service.UserService;

@RestController
@RequestMapping("/api/admin/store")
public class AdminStoreController {
	
	@Autowired
	private StoreService storeService;
	
	// -----------------------
    // Thống kê các cửa hàng
    // -----------------------
    @GetMapping("/stats")
    public StoreStats getStoreStatistics() {
        return storeService.getStoreStatistics();
    }
    
 // -----------------------
    // Lấy danh sách cửa hàng theo phân trang
    // -----------------------
    @GetMapping
    public Page<StoreResponse> getStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
    	
    	Boolean active = null;
        if (status != null && !status.isEmpty()) {
            active = Boolean.parseBoolean(status); // chuyển sang Boolean
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return storeService.getStores(pageable, keyword, active);
    	    }

    @PatchMapping("/{storeId}/block")
    public ResponseEntity<?> blockStore(@PathVariable Integer storeId) {
    	storeService.changeBlock(storeId);
        return ResponseEntity.ok().build();
    }
    
    
    // -----------------------
    // Xóa cửa hàng
    // -----------------------
    @DeleteMapping("/{id}")
    public void deleteStore(@PathVariable Integer id) {
        storeService.deleteStore(id);
    }
	
}
