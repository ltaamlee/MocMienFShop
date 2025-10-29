package mocmien.com.controller.vendor;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import mocmien.com.dto.response.order.*;
import mocmien.com.enums.OrderStatus;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.VendorOrderService;

@RestController
@RequestMapping("/api/vendor/orders")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorOrdersController {

	private final VendorOrderService service;

	public VendorOrdersController(VendorOrderService service) {
		this.service = service;
	}

	private Integer vendorUserId(CustomUserDetails ud) {
		return (ud != null) ? ud.getUserId() : null;
	}

	// Danh sách (filter theo keyword & status, phân trang)
	@GetMapping
	public Page<VendorOrderListItemResponse> list(@AuthenticationPrincipal CustomUserDetails ud,
			@RequestParam(defaultValue = "") String keyword, @RequestParam(required = false) OrderStatus status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		return service.list(vendorUserId(ud), keyword, status, pageable);
	}

	// Chi tiết
	@GetMapping("/{id}")
	public VendorOrderDetailResponse detail(@AuthenticationPrincipal CustomUserDetails ud, @PathVariable String id) {
		return service.detail(vendorUserId(ud), id);
	}

	// Cập nhật trạng thái: NEW->PENDING hoặc PENDING->CONFIRMED
	@PutMapping("/{id}/status")
	public ResponseEntity<?> updateStatus(@AuthenticationPrincipal CustomUserDetails ud, @PathVariable String id,
			@RequestParam("to") OrderStatus to) {
		service.updateStatus(vendorUserId(ud), id, to);
		return ResponseEntity.noContent().build();
	}
}
