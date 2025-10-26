package mocmien.com.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mocmien.com.entity.Promotion;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.enums.PromotionType;

public interface PromotionRepository extends JpaRepository<Promotion, Integer>{
	// -----------------------
    // CRUD cơ bản
    // -----------------------

    // -----------------------
    // Tìm kiếm nâng cao
    // -----------------------
    Optional<Promotion> findByName(String name);
    List<Promotion> findByStore(Store store);
    List<Promotion> findByUser(User user);
    List<Promotion> findByType(PromotionType type);
    List<Promotion> findByIsActive(Boolean isActive);

    List<Promotion> findByStoreAndIsActive(Store store, Boolean isActive);
    List<Promotion> findByTypeAndIsActive(PromotionType type, Boolean isActive);

    // Tìm promotion đang chạy (trong khoảng thời gian hiện tại)
    @Query("SELECT p FROM Promotion p WHERE p.startDate <= :now AND p.endDate >= :now AND p.isActive = true")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.store = :store AND p.startDate <= :now AND p.endDate >= :now AND p.isActive = true")
    List<Promotion> findActivePromotionsByStore(@Param("store") Store store, @Param("now") LocalDateTime now);

    // -----------------------
    // Kích hoạt / hủy kích hoạt
    // -----------------------
    @Query("UPDATE Promotion p SET p.isActive = true WHERE p.id = :id")
    void activatePromotion(@Param("id") Integer id);

    @Query("UPDATE Promotion p SET p.isActive = false WHERE p.id = :id")
    void deactivatePromotion(@Param("id") Integer id);

    // -----------------------
    // Thống kê
    // -----------------------
    long countByIsActive(Boolean isActive);
    long countByStore(Store store);
    long countByType(PromotionType type);

    @Query("SELECT SUM(p.value) FROM Promotion p WHERE p.store = :store")
    BigDecimal sumValueByStore(@Param("store") Store store);

    @Query("SELECT SUM(p.value) FROM Promotion p WHERE p.isActive = true")
    BigDecimal sumValueActivePromotions();

    // -----------------------
    // Phân trang
    // -----------------------
    Page<Promotion> findByIsActive(Boolean isActive, Pageable pageable);
    Page<Promotion> findByStore(Store store, Pageable pageable);
    Page<Promotion> findByType(PromotionType type, Pageable pageable);

    // -----------------------
    // Sắp xếp (Sort)
    // -----------------------
    List<Promotion> findByIsActive(Boolean isActive, Sort sort);
    List<Promotion> findByStore(Store store, Sort sort);
    List<Promotion> findByType(PromotionType type, Sort sort);

    // -----------------------
    // Tìm kiếm tổng hợp
    // -----------------------
    Page<Promotion> findByStoreAndTypeAndIsActive(Store store, PromotionType type, Boolean isActive, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE " +
           "(:store IS NULL OR p.store = :store) AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:isActive IS NULL OR p.isActive = :isActive) AND " +
           "(:start IS NULL OR p.startDate >= :start) AND " +
           "(:end IS NULL OR p.endDate <= :end)")
    Page<Promotion> searchPromotions(
            @Param("store") Store store,
            @Param("type") PromotionType type,
            @Param("isActive") Boolean isActive,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}
