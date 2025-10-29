package mocmien.com.controller.vendor;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import mocmien.com.dto.request.promotion.VendorPromotionCreateRequest;
import mocmien.com.dto.request.promotion.VendorPromotionStatusRequest;
import mocmien.com.dto.request.promotion.VendorPromotionUpdateRequest;
import mocmien.com.dto.response.promotion.VendorPromotionDetailResponse;
import mocmien.com.dto.response.promotion.VendorPromotionListItemResponse;
import mocmien.com.dto.response.promotion.VendorPromotionStatsResponse;
import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.VendorPromotionService;

@RestController
@RequestMapping("/api/vendor/promotions")
@PreAuthorize("hasRole('VENDOR')")
public class VendorPromotionsController {

	private final VendorPromotionService service;

	@Autowired
	public VendorPromotionsController(VendorPromotionService service) {
		this.service = service;
	}

	private Integer uid(CustomUserDetails ud) {
		return ud.getUserId();
	}

	// LIST + FILTER
	@GetMapping
	public Page<VendorPromotionListItemResponse> list(@AuthenticationPrincipal CustomUserDetails ud,
			@RequestParam(defaultValue = "") String keyword, @RequestParam(required = false) PromotionStatus status,
			@RequestParam(required = false) PromotionType type, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return service.list(uid(ud), keyword, status, type, pageable);
	}

	// STATS
	@GetMapping("/stats")
	public VendorPromotionStatsResponse stats(@AuthenticationPrincipal CustomUserDetails ud) {
		return service.stats(uid(ud));
	}

	// DETAIL
	@GetMapping("/{id}")
	public VendorPromotionDetailResponse detail(@AuthenticationPrincipal CustomUserDetails ud,
			@PathVariable Integer id) {
		return service.detail(uid(ud), id);
	}

	// CREATE
	@PostMapping("/add")
	public ResponseEntity<?> create(@AuthenticationPrincipal CustomUserDetails ud,
			@Valid @RequestBody VendorPromotionCreateRequest req) {
		Integer id = service.create(uid(ud), req);
		return ResponseEntity.ok(id);
	}

	// UPDATE
	@PutMapping("/edit/{id}")
	public VendorPromotionDetailResponse update(@AuthenticationPrincipal CustomUserDetails ud, @PathVariable Integer id,
			@Valid @RequestBody VendorPromotionUpdateRequest req) {
		return service.update(uid(ud), id, req);
	}

	// STATUS
	@PutMapping("/{id}/status")
	public ResponseEntity<?> updateStatus(@AuthenticationPrincipal CustomUserDetails ud, @PathVariable Integer id,
			@Valid @RequestBody VendorPromotionStatusRequest req) {
		service.updateStatus(uid(ud), id, req.getStatus());
		return ResponseEntity.noContent().build();
	}

	// DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@AuthenticationPrincipal CustomUserDetails ud, @PathVariable Integer id) {
		service.delete(uid(ud), id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/options/products")
	public List<mocmien.com.repository.PromotionRepository.IdName> productOptions(
			@AuthenticationPrincipal CustomUserDetails ud) {
		return service.productOptions(uid(ud));
	}
}
