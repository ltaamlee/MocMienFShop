package mocmien.com.controller.vendor;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocmien.com.dto.request.product.ProductCreateRequest;
import mocmien.com.dto.request.product.ProductUpdateRequest;
import mocmien.com.dto.response.product.ProductDetailResponse;
import mocmien.com.dto.response.product.ProductListItemResponse;
import mocmien.com.entity.Category;
import mocmien.com.entity.Product;
import mocmien.com.entity.ProductImage;
import mocmien.com.entity.Store;
import mocmien.com.enums.ProductStatus;
import mocmien.com.repository.CategoryRepository;
import mocmien.com.repository.ProductRepository;
import mocmien.com.repository.StoreRepository;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.ProductService;

@RestController
@RequestMapping("/api/vendor/products")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorProductsController {

	private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProductService queryService;
    private final StoreRepository storeRepo;

    public VendorProductsController(ProductRepository productRepo,
                                    CategoryRepository categoryRepo,
                                    ProductService queryService,
                                    StoreRepository storeRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.queryService = queryService;
        this.storeRepo = storeRepo;
    }

	private Store currentStore() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var principal = auth != null ? auth.getPrincipal() : null;
		if (principal instanceof CustomUserDetails cud) {
			Integer userId = cud.getUserId();
			return storeRepo.findByVendorUserId(userId) // gọi method mới bên dưới
					.orElseThrow(() -> new IllegalStateException(
							"Tài khoản chưa có cửa hàng. Vui lòng đăng ký shop trước!"));
		}
		throw new IllegalStateException("Không xác định người dùng đăng nhập");
	}

	// ===== LẤY DANH MỤC (không cần CategoryController riêng)
	@GetMapping(value = "/categories", produces = "application/json")
	public List<SimpleCategory> categories(@RequestParam(defaultValue = "true") boolean active) {
		var list = active ? categoryRepo.findByIsActiveTrueOrderByCategoryNameAsc() : categoryRepo.findAll();
		return list.stream().map(c -> new SimpleCategory(c.getId(), c.getCategoryName())).toList();
	}

	public static record SimpleCategory(Integer id, String categoryName) {
	}

	// ===== LIST
	@GetMapping
	public Page<ProductListItemResponse> list(@RequestParam(defaultValue = "") String keyword,
			@RequestParam(required = false) Integer categoryId, @RequestParam(required = false) ProductStatus status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt", "createdAt"));
		Store store = currentStore();
		return queryService.list(keyword, categoryId, status, store, pageable);
	}

	// ===== DETAIL
	@GetMapping("/{id}")
	public ProductDetailResponse detail(@PathVariable Integer id) {
		return queryService.detail(id);
	}

	// ===== CREATE (auto promotionalPrice = price)

	@PostMapping
	@Transactional
	public ResponseEntity<?> create(@Valid @RequestBody ProductCreateRequest req) {
		var store = currentStore(); // <-- LẤY STORE
		var cat = categoryRepo.findById(req.getCategoryId()).orElseThrow();

		var p = new Product();
		p.setStore(store); // <-- SET BẮT BUỘC
		p.setCategory(cat);
		p.setProductName(req.getProductName());
		p.setSlug(uniqueSlug(slugify(req.getProductName()), store));
		p.setPrice(req.getPrice());
		p.setPromotionalPrice(req.getPrice());
		p.setSize(req.getSize());
		p.setStock(req.getStock());
		p.setIsActive(Boolean.TRUE.equals(req.getIsActive()));
		applyStatusFlags(p, req.getStatus(), req.getStock());
		if (req.getImageUrls() != null && !req.getImageUrls().isEmpty()) {
			List<ProductImage> imgs = new ArrayList<>();
			for (int i = 0; i < req.getImageUrls().size(); i++) {
				ProductImage img = new ProductImage();
				img.setProduct(p);
				img.setImageUrl(req.getImageUrls().get(i));
				img.setIsDefault(i == 0);
				img.setImageIndex(i);
				imgs.add(img);
			}
			p.setImages(imgs);
		}
		productRepo.save(p);
		return ResponseEntity.ok(queryService.detail(p.getId()));
	}

	// ===== UPDATE (auto promotionalPrice = price)

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody ProductUpdateRequest req) {
		var p = productRepo.findById(id).orElseThrow();
		var cat = categoryRepo.findById(req.getCategoryId()).orElseThrow();

		// p.setStore(...) giữ nguyên store cũ, thường không đổi
		p.setCategory(cat);
		p.setProductName(req.getProductName());
		p.setSlug(uniqueSlug(slugify(req.getProductName()), p.getStore(), p.getId()));
		p.setPrice(req.getPrice());
		p.setPromotionalPrice(req.getPrice());
		p.setSize(req.getSize());
		p.setStock(req.getStock());
		if (req.getIsActive() != null)
			p.setIsActive(req.getIsActive());
		if (req.getStatus() != null)
			applyStatusFlags(p, req.getStatus(), req.getStock());
		if (req.getImageUrls() != null) {
		    List<ProductImage> existingImages = p.getImages();
		    if (existingImages == null) {
		        existingImages = new ArrayList<>();
		        p.setImages(existingImages);
		    } else {
		        existingImages.clear();
		    }

		    for (int i = 0; i < req.getImageUrls().size(); i++) {
		        ProductImage img = new ProductImage();
		        img.setProduct(p);
		        img.setImageUrl(req.getImageUrls().get(i));
		        img.setIsDefault(i == 0);
		        img.setImageIndex(i);
		        existingImages.add(img);
		    }
		}

		productRepo.save(p);
		return ResponseEntity.ok(queryService.detail(p.getId()));
	}

	// ===== DELETE
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		var p = productRepo.findById(id).orElseThrow();
		productRepo.delete(p);
		return ResponseEntity.noContent().build();
	}

	// ===== Helpers

	// Overload cho create (không có ignoreId)
	private String uniqueSlug(String base, Store store) {
	    return uniqueSlug(base, store, null);
	}

	// Hàm chính
	private String uniqueSlug(String base, Store store, Integer ignoreId) {
	    String s = base;
	    int i = 1;
	    while (productRepo.findBySlugAndStore(s, store)
	            .filter(p -> ignoreId == null || !p.getId().equals(ignoreId))
	            .isPresent()) {
	        s = base + "-" + (++i);
	    }
	    return s;
	}


	private String slugify(String s) {
	    if (s == null)
	        return null;
	    String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
	    String withoutAccent = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	    withoutAccent = withoutAccent.replaceAll("đ", "d").replaceAll("Đ", "D");
	    String slug = withoutAccent.toLowerCase()
	            .replaceAll("[^a-z0-9\\s-]", "")
	            .replaceAll("\\s+", "-")
	            .replaceAll("-{2,}", "-")
	            .replaceAll("^-|-$", ""); 
	    return slug;
	}

	private void applyStatusFlags(Product p, ProductStatus st, Integer stock) {
		if (st == null)
			st = ProductStatus.SELLING;
		int safeStock = stock != null ? stock : (p.getStock() != null ? p.getStock() : 0);
		switch (st) {
		case SELLING -> {
			p.setIsActive(true);
			p.setIsSelling(true);
			p.setIsAvailable(safeStock > 0);
		}
		case STOPPED -> {
			p.setIsActive(true);
			p.setIsSelling(false);
			p.setIsAvailable(safeStock > 0);
		}
		case OUT_OF_STOCK -> {
			p.setIsActive(true);
			p.setIsSelling(true);
			p.setIsAvailable(false);
			if (safeStock > 0)
				p.setStock(0);
		}
		}
	}

	@RestControllerAdvice
	public class ApiExceptionHandler {
		@ExceptionHandler(Exception.class)
		public ResponseEntity<String> handle(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
