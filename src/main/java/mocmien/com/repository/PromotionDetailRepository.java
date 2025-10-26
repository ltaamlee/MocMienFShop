package mocmien.com.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Level;
import mocmien.com.entity.Product;
import mocmien.com.entity.Promotion;
import mocmien.com.entity.PromotionDetail;
import mocmien.com.enums.PromotionType;
import mocmien.com.enums.Rank;

@Repository
public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {

    // -----------------------
    // CRUD cơ bản
    // -----------------------

    // -----------------------
    // Tìm kiếm theo Promotion
    // -----------------------
    List<PromotionDetail> findByPromotion(Promotion promotion);
    Page<PromotionDetail> findByPromotion(Promotion promotion, Pageable pageable);

    // -----------------------
    // Tìm kiếm theo Product
    // -----------------------
    List<PromotionDetail> findByProduct(Product product);
    Page<PromotionDetail> findByProduct(Product product, Pageable pageable);

    // -----------------------
    // Tìm kiếm theo Level (hạng khách hàng)
    // -----------------------
    List<PromotionDetail> findByLevel(Rank rank);
    Page<PromotionDetail> findByLevel(Rank rank, Pageable pageable);

    // -----------------------
    // Tìm kiếm theo loại khuyến mãi
    // -----------------------
    List<PromotionDetail> findByType(PromotionType type);
    Page<PromotionDetail> findByType(PromotionType type, Pageable pageable);

    // -----------------------
    // Tìm kiếm khuyến mãi đang hoạt động
    // -----------------------
    List<PromotionDetail> findByIsActiveTrue();
    Page<PromotionDetail> findByIsActiveTrue(Pageable pageable);

    // -----------------------
    // Tìm kiếm khuyến mãi theo thời gian áp dụng
    // -----------------------
    List<PromotionDetail> findByStartDateBeforeAndEndDateAfter(LocalDateTime now1, LocalDateTime now2);

    // -----------------------
    // Thống kê
    // -----------------------
    long countByPromotion(Promotion promotion);
    long countByProduct(Product product);
    long countByLevel(Level level);
    long countByIsActive(Boolean isActive);

    // -----------------------
    // Kiểm tra tồn tại
    // -----------------------
    boolean existsByPromotionAndProduct(Promotion promotion, Product product);
    boolean existsByPromotionAndLevel(Promotion promotion, Level level);
}