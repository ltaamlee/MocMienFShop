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
    // Thống kê số lượng cửa hàng
    // -----------------------
    long countByIsActive(Boolean isActive);
    long countByIsOpen(Boolean isOpen);
    List<Store> findByStoreNameContainingIgnoreCase(String keyword);
    Page<Store> findByStoreNameContainingIgnoreCaseAndIsActive(String keyword, boolean isActive, Pageable pageable);
    Page<Store> findByIsActive(boolean isActive, Pageable pageable);

    // -----------------------
    // Cập nhật ví điện tử + rating + điểm trong 1 transaction
    // -----------------------
    @Transactional
    @Modifying
    @Query("""
        UPDATE Store s 
        SET s.eWallet = s.eWallet + :revenue,
            s.rating  = s.rating  + :rating,
            s.point   = s.point   + :points
        WHERE s.id = :storeId
    """)
    int updateAfterOrder(
            @Param("storeId") Integer storeId,
            @Param("revenue") BigDecimal revenue,
            @Param("rating") BigDecimal rating,
            @Param("points") Integer points
    );
    
    // Các hàm tìm kiếm hợp lệ
    List<Store> findByVendor(User vendor);
    Optional<Store> findByIdAndVendor(Integer id, User vendor);
    List<Store> findByIsOpen(Boolean isOpen);
    Page<Store> findByStoreNameContainingIgnoreCase(String keyword, Pageable pageable);
    List<Store> findByIsActive(Boolean isActive);
    Optional<Store> findByStoreName(String storeName);
    List<Store> findByRatingGreaterThanEqual(BigDecimal rating);
    List<Store> findByPointGreaterThanEqual(Integer point);

}