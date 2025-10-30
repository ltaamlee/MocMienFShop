package mocmien.com.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import mocmien.com.dto.response.product.ProductListItemResponse;
import mocmien.com.entity.Product;
import mocmien.com.enums.ProductStatus;
import mocmien.com.repository.ProductRepository;
import mocmien.com.service.AdminProductService;

@RestController
@RequestMapping("/api/admin/product")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {

	@Autowired
	private AdminProductService adminProductService;

	@Autowired
	private ProductRepository productRepo;

	@GetMapping
	public Page<ProductListItemResponse> list(
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(required = false) Integer storeId,
			@RequestParam(required = false) ProductStatus status,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt", "createdAt"));
		return adminProductService.listAdmin(keyword, categoryId, storeId, status, pageable);
	}

	@PatchMapping("/{id}/ban")
	public ResponseEntity<?> ban(@PathVariable Integer id) {
		Product p = productRepo.findById(id).orElseThrow();
		p.setIsActive(false);
		productRepo.save(p);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/unban")
	public ResponseEntity<?> unban(@PathVariable Integer id) {
		Product p = productRepo.findById(id).orElseThrow();
		p.setIsActive(true);
		productRepo.save(p);
		return ResponseEntity.noContent().build();
	}
}