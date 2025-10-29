package mocmien.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mocmien.com.dto.request.delivery.DeliveryRequest;
import mocmien.com.dto.response.admin.DeliveryResponse;
import mocmien.com.dto.response.admin.DeliveryStats;
import mocmien.com.dto.response.store.StoreStats;
import mocmien.com.service.AdminDeliveryService;
import mocmien.com.service.StoreService;

@RestController
@RequestMapping("/api/admin/delivery")
public class AdminDeliveryController {

	@Autowired
	private AdminDeliveryService adminDeliveryService;

	// -----------------------
	// Thống kê các đơn vị vận chuyển
	// -----------------------
	@GetMapping("/stats")
	public DeliveryStats getDeliveryStatistics() {
		return adminDeliveryService.getDeliveryStatistics();
	}
	
	//Render bảng
	@GetMapping 
    public ResponseEntity<Page<DeliveryResponse>> getDeliveries(Pageable pageable) {
        Page<DeliveryResponse> deliveries = adminDeliveryService.findAllDeliveries(pageable);
        return ResponseEntity.ok(deliveries);
    }

	// Thêm đơn vị vận chuyển
	@PostMapping
	public ResponseEntity<DeliveryResponse> createDelivery(@Valid @RequestBody DeliveryRequest request) {
		// @Valid kích hoạt quá trình kiểm tra ràng buộc (validation) đã định nghĩa
		// trong DeliveryRequest

		DeliveryResponse response = adminDeliveryService.createDelivery(request);

		// Trả về mã 201 Created và DTO phản hồi
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	
	@GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Integer id) {
        DeliveryResponse response = adminDeliveryService.findDeliveryResponseById(id);
        return ResponseEntity.ok(response);
    }
    
    // ----------------------------------------------------
    // 5. CẬP NHẬT (PUT /{id}) - Phục vụ cho chức năng Sửa
    // ----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponse> updateDelivery(
            @PathVariable Integer id, 
            @Valid @RequestBody DeliveryRequest request) {
        
        DeliveryResponse response = adminDeliveryService.updateDelivery(id, request);
        return ResponseEntity.ok(response);
    }
    
    // ----------------------------------------------------
    // 6. XÓA (DELETE /{id}) - Phục vụ cho chức năng Xóa
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Integer id) {
    	adminDeliveryService.delete(id);
        // Trả về mã 204 No Content khi xóa thành công
        return ResponseEntity.noContent().build();
    }

}
