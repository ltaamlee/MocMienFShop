package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.dto.response.product.ProductDetailResponse;
import mocmien.com.dto.response.product.ProductListItemResponse;
import mocmien.com.entity.Category;
import mocmien.com.entity.Product;
import mocmien.com.entity.ProductImage;
import mocmien.com.entity.Store;
import mocmien.com.enums.ProductStatus;
import mocmien.com.repository.CategoryRepository;
import mocmien.com.repository.ProductRepository;
import mocmien.com.service.ProductService;
import org.springframework.data.domain.*;

import lombok.RequiredArgsConstructor;

@Service
@Transactional 
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepo;
	private final CategoryRepository categoryRepo;

	public ProductServiceImpl(ProductRepository productRepo, CategoryRepository categoryRepo) {
		this.productRepo = productRepo;
		this.categoryRepo = categoryRepo;
	}

	// ===================== TIỆN ÍCH MAP STATUS =====================
	private ProductStatus statusOf(Product p) {
		if (p.getIsActive() == null || !p.getIsActive() || p.getIsSelling() == null || !p.getIsSelling()) {
			return ProductStatus.STOPPED;
		}
		Integer stock = p.getStock();
		boolean available = p.getIsAvailable() != null && p.getIsAvailable();
		if (!available || stock == null || stock <= 0)
			return ProductStatus.OUT_OF_STOCK;
		return ProductStatus.SELLING;
	}

	private Comparator<ProductImage> imageOrder() {
		return (a, b) -> {
			Integer ia = (a == null) ? null : a.getImageIndex();
			Integer ib = (b == null) ? null : b.getImageIndex();
			if (ia == null && ib == null)
				return 0;
			if (ia == null)
				return 1;
			if (ib == null)
				return -1;
			return Integer.compare(ia, ib);
		};
	}

	// ===== LIST
	@Override
	public Page<ProductListItemResponse> list(String keyword, Integer categoryId, ProductStatus status,
			Pageable pageable) {
		if (keyword == null)
			keyword = "";
		Page<Product> page;
		if (categoryId != null) {
			Category cat = categoryRepo.findById(categoryId).orElse(null);
			page = productRepo.findByCategory(cat, pageable);
		} else {
			page = productRepo.findByProductNameContainingIgnoreCase(keyword, pageable);
		}

		var content = page.getContent().stream().map(p -> {
			ProductListItemResponse dto = new ProductListItemResponse();
			dto.setId(p.getId());
			dto.setProductName(p.getProductName());
			dto.setCategoryName(p.getCategory() != null ? p.getCategory().getCategoryName() : null);
			dto.setPrice(p.getPrice());
			dto.setPromotionalPrice(p.getPromotionalPrice());
			dto.setStock(p.getStock());
			dto.setStatus(statusOf(p));
			dto.setIsActive(p.getIsActive());
			dto.setDefaultImage((p.getImages() == null) ? null
					: p.getImages().stream().sorted(imageOrder()).map(ProductImage::getImageUrl).findFirst()
							.orElse(null));
			return dto;
		}).filter(d -> status == null || d.getStatus() == status).collect(Collectors.toList());

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	// ===== DETAIL
	@Override
	public ProductDetailResponse detail(Integer id) {
		Product p = productRepo.findById(id).orElseThrow();

		ProductDetailResponse dto = new ProductDetailResponse();
		dto.setId(p.getId());
		dto.setProductName(p.getProductName());
		dto.setCategoryId(p.getCategory() != null ? p.getCategory().getId() : null);
		dto.setCategoryName(p.getCategory() != null ? p.getCategory().getCategoryName() : null);
		dto.setPrice(p.getPrice());
		dto.setPromotionalPrice(p.getPromotionalPrice());
		dto.setSize(p.getSize());
		dto.setStock(p.getStock());
		dto.setSold(p.getSold());
		dto.setStatus(statusOf(p));
		dto.setIsActive(p.getIsActive());
		dto.setImageUrls((p.getImages() == null) ? List.of()
				: p.getImages().stream().sorted(imageOrder()).map(ProductImage::getImageUrl)
						.collect(Collectors.toList()));
		return dto;
	}

	// ===================== CƠ BẢN =====================
	@Override
	public List<Product> getAllProducts() {
		return productRepo.findAll();
	}

	@Override
	public Optional<Product> getProductById(Integer id) {
		return productRepo.findById(id);
	}

	@Override
	public Optional<Product> getBySlug(String slug, Store store) {
	    return productRepo.findBySlugAndStore(slug, store);
	}

	@Override
	public Product saveProduct(Product product) {
		return productRepo.save(product);
	}

	@Override
	public void deleteProduct(Integer id) {
		productRepo.deleteById(id);
	}

	// ===================== LỌC / TÌM KIẾM =====================
	@Override
	public List<Product> searchByName(String keyword) {
		return productRepo.findByProductNameContainingIgnoreCase(keyword);
	}

	@Override
	public List<Product> findByCategory(Category category) {
		return productRepo.findByCategory(category);
	}

	@Override
	public List<Product> findByStore(Store store) {
		return productRepo.findByStore(store);
	}

	@Override
	public List<Product> findByStoreAndCategory(Store store, Category category) {
		return productRepo.findByStoreAndCategory(store, category);
	}

	@Override
	public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
		return productRepo.findByPriceBetween(minPrice, maxPrice);
	}

	// ===================== PHÂN TRANG =====================
	@Override
	public Page<Product> searchByName(String keyword, Pageable pageable) {
		return productRepo.findByProductNameContainingIgnoreCase(keyword, pageable);
	}

	@Override
	public Page<Product> findByCategory(Category category, Pageable pageable) {
		return productRepo.findByCategory(category, pageable);
	}

	@Override
	public Page<Product> findByStore(Store store, Pageable pageable) {
		return productRepo.findByStore(store, pageable);
	}

	@Override
	public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
		return productRepo.findByPriceBetween(minPrice, maxPrice, pageable);
	}

	// ===================== DANH SÁCH NỔI BẬT =====================
	@Override
	public List<Product> getTopRated(BigDecimal minRating) {
		return productRepo.findTopRated(minRating);
	}

	@Override
	public List<Product> getTopSelling(int limit) {
		return productRepo.findTopSelling(limit);
	}

	@Override
	public List<Product> getDiscountedProducts() {
		return productRepo.findDiscountedProducts();
	}

	// ===================== TÌM KIẾM NÂNG CAO =====================
	@Override
	public List<Product> searchAdvanced(String keyword, Category category, BigDecimal minPrice, BigDecimal maxPrice) {
		return productRepo.searchAdvanced(keyword, category, minPrice, maxPrice);
	}

	// ===================== THỐNG KÊ =====================
	@Override
	public long countDiscounted() {
		return productRepo.countDiscountedProducts();
	}

	@Override
	public long countOutOfStock() {
		return productRepo.countOutOfStock();
	}

	@Override
	public BigDecimal averageRatingByStore(Store store) {
		return productRepo.averageRatingByStore(store);
	}

	// ====== Builder tạm cho ProductListItemResponse (nếu em dùng @Builder của
	// Lombok thì không cần class này) ======
	private static class ProductListItemResponseBuilder {
		private final ProductListItemResponse dto = new ProductListItemResponse();

		public ProductListItemResponseBuilder id(Integer v) {
			dto.setId(v);
			return this;
		}

		public ProductListItemResponseBuilder productName(String v) {
			dto.setProductName(v);
			return this;
		}

		public ProductListItemResponseBuilder categoryName(String v) {
			dto.setCategoryName(v);
			return this;
		}

		public ProductListItemResponseBuilder price(BigDecimal v) {
			dto.setPrice(v);
			return this;
		}

		public ProductListItemResponseBuilder promotionalPrice(BigDecimal v) {
			dto.setPromotionalPrice(v);
			return this;
		}

		public ProductListItemResponseBuilder stock(Integer v) {
			dto.setStock(v);
			return this;
		}

		public ProductListItemResponseBuilder status(ProductStatus v) {
			dto.setStatus(v);
			return this;
		}

		public ProductListItemResponseBuilder defaultImage(String v) {
			dto.setDefaultImage(v);
			return this;
		}

		public ProductListItemResponseBuilder isActive(Boolean v) {
			dto.setIsActive(v);
			return this;
		}

		public ProductListItemResponse build() {
			return dto;
		}
	}

	@Override
	public Page<ProductListItemResponse> list(String keyword, Integer categoryId, ProductStatus status, Store store,
			Pageable pageable) {
		final String kw = (keyword == null) ? "" : keyword;

	    Page<Product> page;

	    if (categoryId != null) {
	        Category cat = categoryRepo.findById(categoryId).orElse(null);
	        page = productRepo.findByStoreAndCategory(store, cat, pageable);
	    } else {
	        page = productRepo.findByStore(store, pageable);
	        // Lọc keyword trực tiếp trong stream
	        page = new PageImpl<>(
	            page.getContent().stream()
	                .filter(p -> p.getProductName().toLowerCase().contains(kw.toLowerCase()))
	                .collect(Collectors.toList()),
	            pageable,
	            page.getTotalElements()
	        );
	    }

	    var content = page.getContent().stream().map(p -> {
	        ProductListItemResponse dto = new ProductListItemResponse();
	        dto.setId(p.getId());
	        dto.setProductName(p.getProductName());
	        dto.setCategoryName(p.getCategory() != null ? p.getCategory().getCategoryName() : null);
	        dto.setPrice(p.getPrice());
	        dto.setPromotionalPrice(p.getPromotionalPrice());
	        dto.setStock(p.getStock());
	        dto.setStatus(statusOf(p));
	        dto.setIsActive(p.getIsActive());
	        dto.setDefaultImage((p.getImages() == null) ? null
	                : p.getImages().stream().sorted(imageOrder()).map(ProductImage::getImageUrl).findFirst().orElse(null));
	        return dto;
	    }).filter(d -> status == null || d.getStatus() == status).collect(Collectors.toList());

	    return new PageImpl<>(content, pageable, page.getTotalElements());
	}
}