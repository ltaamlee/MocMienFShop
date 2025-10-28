package mocmien.com.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mocmien.com.dto.product.ProductRowVM;
import mocmien.com.dto.response.customer.ProductDetailResponse;
import mocmien.com.entity.Category;
import mocmien.com.entity.Product;
import mocmien.com.entity.Store;

public interface ProductService {

    // ===================== CƠ BẢN =====================
    List<Product> getAllProducts();
    Optional<Product> getProductById(Integer id);
    Optional<Product> getBySlug(String slug);
    Product saveProduct(Product product);
    void deleteProduct(Integer id);

    // ===================== LỌC / TÌM KIẾM =====================
    List<Product> searchByName(String keyword);
    List<Product> findByCategory(Category category);
    List<Product> findByStore(Store store);
    List<Product> findByStoreAndCategory(Store store, Category category);
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    // ===================== PHÂN TRANG =====================
    Page<Product> searchByName(String keyword, Pageable pageable);
    Page<Product> findByCategory(Category category, Pageable pageable);
    Page<Product> findByStore(Store store, Pageable pageable);
    Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // ===================== DANH SÁCH NỔI BẬT =====================
    List<Product> getTopRated(BigDecimal minRating);
    List<Product> getTopSelling(int limit);
    List<Product> getDiscountedProducts();

    // ===================== TÌM KIẾM NÂNG CAO =====================
    List<Product> searchAdvanced(String keyword, Category category, BigDecimal minPrice, BigDecimal maxPrice);

    // ===================== THỐNG KÊ =====================
    long countDiscounted();
    long countOutOfStock();
    BigDecimal averageRatingByStore(Store store);
    
    //=================================================
    List<ProductRowVM> getAllProductRows();
    ProductDetailResponse getProductDetailById(Integer id);

}
