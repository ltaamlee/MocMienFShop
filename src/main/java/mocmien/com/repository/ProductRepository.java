package mocmien.com.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Category;
import mocmien.com.entity.Product;
import mocmien.com.entity.Store;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	

    List<Product> findByStore_IdAndIdIn(Integer storeId, Collection<Integer> ids);
    interface IdName {
        Integer getId();
        String getProductName();
    }

    @Query("""
        select p.id as id, p.productName as productName
        from Product p
        where p.store.id = :storeId and
              (p.isActive = true or p.isActive is null) and
              (p.isAvailable = true or p.isAvailable is null)
        order by p.productName asc
    """)
    List<IdName> findOptionsByStoreId(Integer storeId);

	 // ============================================================
    // 🔹 CRUD cơ bản
    // ============================================================
    @EntityGraph(attributePaths = {"images", "store", "category"})
    Optional<Product> findBySlugAndStore(String slug, Store store);




    // ============================================================
    // 🔹 TÌM KIẾM / LỌC CƠ BẢN
    // ============================================================
    @EntityGraph(attributePaths = {"images"})
    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    @EntityGraph(attributePaths = {"images", "category"})
    List<Product> findByCategory(Category category);

    @EntityGraph(attributePaths = {"images", "store"})
    List<Product> findByStore(Store store);

    @EntityGraph(attributePaths = {"images"})
    List<Product> findByIsSellingTrue();

    @EntityGraph(attributePaths = {"images"})
    List<Product> findByIsAvailableTrue();

    @EntityGraph(attributePaths = {"images", "category", "store"})
    List<Product> findByStoreAndCategory(Store store, Category category);

    @EntityGraph(attributePaths = {"images"})
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // ============================================================
    // PHÂN TRANG + ENTITY GRAPH
    // ============================================================
    @EntityGraph(attributePaths = {"images", "store", "category"})
    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"images", "store"})
    Page<Product> findByStore(Store store, Pageable pageable);

    @EntityGraph(attributePaths = {"images", "category"})
    Page<Product> findByCategory(Category category, Pageable pageable);

    @EntityGraph(attributePaths = {"images", "category", "store"})
	Page<Product> findByStoreAndCategory(Store store, Category cat, Pageable pageable);

    
    @EntityGraph(attributePaths = {"images"})
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // ============================================================
    // 🔹 KIỂM TRA TỒN TẠI
    // ============================================================
    boolean existsByProductName(String productName);
    boolean existsBySlug(String slug);

    // ============================================================
    // 🔹 THỐNG KÊ / ĐẾM
    // ============================================================
    long countByStore(Store store);
    long countByCategory(Category category);
    long countByIsSellingTrue();
    long countByIsAvailableTrue();

    // ============================================================
    // 🔹 HÀM NÂNG CAO (CUSTOM QUERY)
    // ============================================================

    // 🟢 Lấy sản phẩm theo rating cao nhất
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.rating >= :minRating ORDER BY p.rating DESC")
    List<Product> findTopRated(BigDecimal minRating);

    // 🟢 Lấy top N sản phẩm bán chạy nhất
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images ORDER BY p.sold DESC LIMIT :limit")
    List<Product> findTopSelling(int limit);

    // 🟢 Lấy danh sách sản phẩm đang khuyến mãi (price > promotionalPrice)
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.promotionalPrice < p.price")
    List<Product> findDiscountedProducts();

    // 🟢 Lọc nâng cao (lọc theo khoảng giá + danh mục + keyword)
    @Query("""
        SELECT p FROM Product p 
        LEFT JOIN FETCH p.images 
        WHERE (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:category IS NULL OR p.category = :category)
          AND (p.price BETWEEN :minPrice AND :maxPrice)
    """)
    List<Product> searchAdvanced(String keyword, Category category, BigDecimal minPrice, BigDecimal maxPrice);

    // 🟢 Đếm số sản phẩm đang giảm giá
    @Query("SELECT COUNT(p) FROM Product p WHERE p.promotionalPrice < p.price")
    long countDiscountedProducts();

    // 🟢 Đếm số sản phẩm hết hàng
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isAvailable = false")
    long countOutOfStock();

    // 🟢 Thống kê trung bình rating theo cửa hàng
    @Query("SELECT AVG(p.rating) FROM Product p WHERE p.store = :store")
    BigDecimal averageRatingByStore(Store store);

    long countByCategory_Id(Integer categoryId);

}
