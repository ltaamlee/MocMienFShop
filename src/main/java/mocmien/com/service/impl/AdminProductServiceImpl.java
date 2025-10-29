package mocmien.com.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mocmien.com.dto.response.product.ProductListItemResponse;
import mocmien.com.entity.Product;
import mocmien.com.enums.ProductStatus;
import mocmien.com.repository.ProductRepository;
import mocmien.com.service.AdminProductService;

@Service
@RequiredArgsConstructor // Tự động tạo constructor với các trường final
public class AdminProductServiceImpl implements AdminProductService {

	// ⚠️ Đã thêm từ khóa 'final' để @RequiredArgsConstructor hoạt động
	private final ProductRepository productRepo; 
	
    public AdminProductServiceImpl(ProductRepository productRepo) {
		super();
		this.productRepo = productRepo;
	}

	// ----------------------------------------------------
    // HÀM TIỆN ÍCH: Ánh xạ Entity sang DTO (Sử dụng Setter)
    // ----------------------------------------------------
    private ProductListItemResponse toListItemResponse(Product product) {
        // Giả định ProductListItemResponse có constructor rỗng và setter
        ProductListItemResponse response = new ProductListItemResponse();
        
        // Gán giá trị bằng Setter
        response.setId(product.getId());
        response.setProductName(product.getProductName());
        response.setPrice(product.getPrice());
        response.setPromotionalPrice(product.getPromotionalPrice());
        response.setStock(product.getStock());
        response.setIsActive(product.getIsActive());

        // Lấy thông tin từ các Entity liên quan (Store và Category)
        response.setStoreName(product.getStore() != null ? product.getStore().getStoreName() : "N/A");
        response.setCategoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : "N/A");
        
        return response;
    }
    
	@Override
	public Page<ProductListItemResponse> listAdmin(
			String keyword, 
			Integer categoryId, 
			Integer storeId,
			ProductStatus status, 
			Pageable pageable) {

		// 1. Xây dựng Specification (Bộ lọc)
		Specification<Product> spec = Specification.where(null);

		// Lọc theo từ khóa
		if (keyword != null && !keyword.trim().isEmpty()) {
			spec = spec.and((root, query, cb) -> 
				cb.like(cb.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
		}

		// Lọc theo Danh mục
		if (categoryId != null) {
			spec = spec.and((root, query, cb) -> 
				cb.equal(root.get("category").get("id"), categoryId));
		}

		// Lọc theo Cửa hàng
		if (storeId != null) {
			spec = spec.and((root, query, cb) -> 
				cb.equal(root.get("store").get("id"), storeId));
		}

		// Lọc theo Trạng thái
		if (status != null) {
			spec = spec.and(createStatusSpecification(status));
		}

		// 2. Thực thi truy vấn
		Page<Product> productPage = productRepo.findAll(spec, pageable);

		// 3. Ánh xạ kết quả sang DTO
		return productPage.map(this::toListItemResponse);
	}

	/**
	 * Hàm tiện ích để tạo Specification dựa trên ProductStatus Enum.
	 */
	private Specification<Product> createStatusSpecification(ProductStatus status) {
		return switch (status) {
			case SELLING -> (root, query, cb) -> cb.and(
				cb.isTrue(root.get("isActive")),
				cb.isTrue(root.get("isSelling")),
				cb.isTrue(root.get("isAvailable"))
			);
			case STOPPED -> (root, query, cb) -> cb.and(
				cb.isTrue(root.get("isActive")),
				cb.isFalse(root.get("isSelling"))
			);
			case OUT_OF_STOCK -> (root, query, cb) -> cb.and(
				cb.isTrue(root.get("isActive")),
				cb.isTrue(root.get("isSelling")),
				cb.isFalse(root.get("isAvailable"))
			);
			default -> Specification.where(null);
		};
	}
}