package mocmien.com.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.entity.Store;
import mocmien.com.entity.User;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer>{
	// -----------------------
    // Tìm kiếm theo chủ cửa hàng (vendor)
    // -----------------------
    List<Store> findByVendor(User vendor);
    Optional<Store> findByIdAndVendor(Integer id, User vendor);

    // -----------------------
    // Tìm kiếm theo tên cửa hàng
    // -----------------------
    List<Store> findByStoreNameContainingIgnoreCase(String keyword);
    Optional<Store> findByStoreName(String storeName);

    // -----------------------
    // Trạng thái cửa hàng
    // -----------------------
    List<Store> findByIsActive(Boolean isActive);
    List<Store> findByIsOpen(Boolean isOpen);

    // -----------------------
    // Phân trang
    // -----------------------
    Page<Store> findByIsActive(Boolean isActive, Pageable pageable);
    Page<Store> findByStoreNameContainingIgnoreCase(String keyword, Pageable pageable);

    // -----------------------
    // Thống kê tổng eWallet
    // -----------------------
    @Query("SELECT SUM(s.eWallet) FROM Store s WHERE s.isActive = true")
    BigDecimal totalEWallet();

    @Query("SELECT SUM(s.point) FROM Store s WHERE s.isActive = true")
    Integer totalPoints();

    @Query("SELECT AVG(s.rating) FROM Store s WHERE s.isActive = true")
    BigDecimal averageRating();

    @Query("SELECT s FROM Store s ORDER BY s.rating DESC")
    List<Store> findTopStoresByRating(Pageable pageable);

    @Query("SELECT s FROM Store s ORDER BY s.point DESC")
    List<Store> findTopStoresByPoints(Pageable pageable);

    // -----------------------
    // Cập nhật ví điện tử
    // -----------------------
    @Modifying
    @Transactional
    @Query("UPDATE Store s SET s.eWallet = s.eWallet + :amount WHERE s.id = :storeId")
    int addToEWallet(@Param("storeId") Integer storeId, @Param("amount") BigDecimal amount);

    // -----------------------
    // Cập nhật rating
    // -----------------------
    @Modifying
    @Transactional
    @Query("UPDATE Store s SET s.rating = s.rating + :rating WHERE s.id = :storeId")
    int addRating(@Param("storeId") Integer storeId, @Param("rating") BigDecimal rating);

    // -----------------------
    // Cập nhật điểm
    // -----------------------
    @Modifying
    @Transactional
    @Query("UPDATE Store s SET s.point = s.point + :points WHERE s.id = :storeId")
    int addPoints(@Param("storeId") Integer storeId, @Param("points") Integer points);

    // -----------------------
    // Cập nhật trạng thái mở/đóng cửa hàng
    // -----------------------
    @Modifying
    @Transactional
    @Query("UPDATE Store s SET s.isOpen = :isOpen WHERE s.id = :storeId")
    int setOpenStatus(@Param("storeId") Integer storeId, @Param("isOpen") boolean isOpen);

    // -----------------------
    // Cập nhật trạng thái hoạt động
    // -----------------------
    @Modifying
    @Transactional
    @Query("UPDATE Store s SET s.isActive = :isActive WHERE s.id = :storeId")
    int setActiveStatus(@Param("storeId") Integer storeId, @Param("isActive") boolean isActive);

    // -----------------------
    // Thống kê số lượng cửa hàng
    // -----------------------
    long countByIsActive(Boolean isActive);
    long countByIsOpen(Boolean isOpen);

    // -----------------------
    // Tìm kiếm theo mức điểm, rating
    // -----------------------
    List<Store> findByPointGreaterThanEqual(Integer point);
    List<Store> findByRatingGreaterThanEqual(BigDecimal rating);

    // -----------------------
    // Cập nhật ví điện tử + rating + điểm trong 1 transaction
    // -----------------------
    @Modifying
    @Transactional
    @Query("UPDATE Store s SET s.eWallet = s.eWallet + :revenue, s.rating = s.rating + :rating, s.point = s.point + :points WHERE s.id = :storeId")
    int updateAfterOrder(
        @Param("storeId") Integer storeId,
        @Param("revenue") BigDecimal revenue,
        @Param("rating") BigDecimal rating,
        @Param("points") Integer points
    );
}
