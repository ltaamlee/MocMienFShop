package mocmien.com.controller.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocmien.com.dto.request.product.ProductUpdateRequest; // Dùng lại DTO của Vendor
import mocmien.com.dto.response.product.ProductDetailResponse;
import mocmien.com.dto.response.product.ProductListItemResponse;
import mocmien.com.entity.Product;
import mocmien.com.enums.ProductStatus;
import mocmien.com.repository.CategoryRepository;
import mocmien.com.repository.ProductRepository;
import mocmien.com.service.ProductService; // Giả định Service có các phương thức cho Admin

@RestController
@RequestMapping("/api/admin/products") // Đổi path để tránh trùng
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới có thể truy cập
@RequiredArgsConstructor
public class AdminProductController {

	private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProductService queryService;
    
    
    
    public AdminProductController(ProductRepository productRepo, CategoryRepository categoryRepo,
			ProductService queryService) {
		super();
		this.productRepo = productRepo;
		this.categoryRepo = categoryRepo;
		this.queryService = queryService;
	}

    public static record SimpleCategory(Integer id, String categoryName) {}
    
    @GetMapping(value = "/categories", produces = "application/json")
	public List<SimpleCategory> categories(@RequestParam(defaultValue = "true") boolean active) {
		var list = active ? categoryRepo.findByIsActiveTrueOrderByCategoryNameAsc() : categoryRepo.findAll();
		return list.stream().map(c -> new SimpleCategory(c.getId(), c.getCategoryName())).toList();
	}
    
    // ----------------------------------------------------
    // 1. LIST SẢN PHẨM (Tất cả shops, có lọc)
    // ----------------------------------------------------
    @GetMapping
    public Page<ProductListItemResponse> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer storeId, // 💡 Thêm tham số lọc theo Shop
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        
        // Sắp xếp mặc định: updated/created
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt", "createdAt"));

        // Giả định ProductService có phương thức listAdmin để xử lý logic lọc phức tạp này
        return queryService.listAdmin(keyword, categoryId, storeId, status, pageable);
    }
    
    // ----------------------------------------------------
    // 2. DETAIL (Xem chi tiết sản phẩm của bất kỳ shop nào)
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ProductDetailResponse detail(@PathVariable Integer id) {
        // Tái sử dụng detail của Service vì nó không phụ thuộc vào Store
        return queryService.detail(id); 
    }
    
    // ----------------------------------------------------
    // 3. CẬP NHẬT (Chỉnh sửa sản phẩm của bất kỳ shop nào)
    // ----------------------------------------------------
    /**
     * Admin có thể cập nhật thông tin sản phẩm (chú ý: Admin KHÔNG được đổi StoreId)
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody ProductUpdateRequest req) {
        // Admin chỉ cần kiểm tra sản phẩm tồn tại và có thể cập nhật
        var p = productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm có ID: " + id));
        
        var cat = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));

        // Giữ nguyên Store
        p.setCategory(cat);
        p.setProductName(req.getProductName());
        // Tái sử dụng slugify helper từ Controller Vendor (cần được chuyển thành tiện ích chung)
        // Hiện tại, ta sẽ giả định slugify và uniqueSlug đã được chuyển vào Service hoặc tiện ích chung.
        // Tạm thời bỏ qua logic slugify phức tạp để code Admin đơn giản hơn.
        
        p.setPrice(req.getPrice());
        p.setPromotionalPrice(req.getPrice()); // Giữ nguyên logic auto-set
        p.setSize(req.getSize());
        p.setStock(req.getStock());
        if (req.getIsActive() != null)
            p.setIsActive(req.getIsActive());
        
        // 💡 Giả định logic applyStatusFlags đã được chuyển vào Service
        if (req.getStatus() != null)
            queryService.applyStatusFlags(p, req.getStatus(), req.getStock());
        
        // Logic xử lý hình ảnh (có thể gọi Service xử lý để tránh code lặp)
        if (req.getImageUrls() != null) {
		    queryService.updateProductImages(p, req.getImageUrls()); // 💡 Giả định Service có phương thức này
		}

        productRepo.save(p);
        return ResponseEntity.ok(queryService.detail(p.getId()));
    }
    
    // ----------------------------------------------------
    // 4. XÓA (Xóa sản phẩm của bất kỳ shop nào)
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        // Kiểm tra tồn tại trước khi xóa
        if (!productRepo.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có ID: " + id);
        }
        productRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // 💡 Ghi chú: Cần cập nhật ProductService để thêm listAdmin, applyStatusFlags, và updateProductImages.
    
    @RestControllerAdvice
    public class AdminProductApiExceptionHandler {
        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handle(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}