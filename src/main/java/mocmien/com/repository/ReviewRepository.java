package mocmien.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Product;
import mocmien.com.entity.Review;
import mocmien.com.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // -----------------------
    // CRUD cơ bản
    // -----------------------

    // -----------------------
    // Tìm kiếm theo User
    // -----------------------
    List<Review> findByUser(User user);
    Page<Review> findByUser(User user, Pageable pageable);

    // -----------------------
    // Tìm kiếm theo Product
    // -----------------------
    List<Review> findByProduct(Product product);
    Page<Review> findByProduct(Product product, Pageable pageable);

    // -----------------------
    // Tìm kiếm và phân trang
    // -----------------------
    List<Review> findByRating(Integer rating);
    Page<Review> findByRating(Integer rating, Pageable pageable);

    List<Review> findByProductAndRating(Product product, Integer rating);
    Page<Review> findByProductAndRating(Product product, Integer rating, Pageable pageable);

    // -----------------------
    // Thống kê
    // -----------------------
    long countByProduct(Product product);
    long countByUser(User user);
    long countByRating(Integer rating);

    // Tính trung bình rating của sản phẩm
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    Double getAverageRatingByProduct(@Param("product") Product product);

    // Kiểm tra user đã review sản phẩm chưa
    boolean existsByProductAndUser(Product product, User user);
    
    List<Review> findByProductOrderByCreateAtDesc(Product product);
}

