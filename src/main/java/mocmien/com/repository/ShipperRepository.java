package mocmien.com.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Delivery;
import mocmien.com.entity.Shipper;
import mocmien.com.entity.User;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Integer> {

    // -----------------------
    // CRUD cơ bản
    // -----------------------
    Shipper save(Shipper shipper);
    void deleteById(Integer id);
    Optional<Shipper> findById(Integer id);
    Optional<Shipper> findByUser(User user);

    // -----------------------
    // Tìm kiếm theo trạng thái, tên, xe
    // -----------------------
    List<Shipper> findByIsActive(Boolean isActive);
    List<Shipper> findByFullNameContainingIgnoreCase(String keyword);
    List<Shipper> findByVehicleNumber(String vehicleNumber);
    List<Shipper> findByVehicleType(String vehicleType);
    List<Shipper> findByDelivery(Delivery delivery);

    // -----------------------
    // Thống kê
    // -----------------------
    long countByIsActive(Boolean isActive);
    long countByDelivery(Delivery delivery);
    List<Shipper> findTop10ByOrderByRatingDesc(); // 10 shipper rating cao nhất
    List<Shipper> findTop10ByOrderByTotalDeliveryDesc(); // 10 shipper giao nhiều đơn nhất
    BigDecimal sumEWalletByIsActive(Boolean isActive); // tổng ví điện tử của shipper đang active
    BigDecimal sumEWallet(); // tổng ví điện tử của tất cả shipper

    // -----------------------
    // Phân trang
    // -----------------------
    Page<Shipper> findAll(Pageable pageable);
    Page<Shipper> findByIsActive(Boolean isActive, Pageable pageable);
    Page<Shipper> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);

    // -----------------------
    // Kiểm tra tồn tại
    // -----------------------
    boolean existsByUser(User user);
    boolean existsByVehicleNumber(String vehicleNumber);

    // -----------------------
    // Các phương thức nâng cao
    // -----------------------
    
    // Lọc shipper theo khoảng rating
    List<Shipper> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating);
    Page<Shipper> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating, Pageable pageable);

    // Lọc shipper theo tổng số đơn đã giao
    List<Shipper> findByTotalDeliveryBetween(Integer minDelivery, Integer maxDelivery);
    Page<Shipper> findByTotalDeliveryBetween(Integer minDelivery, Integer maxDelivery, Pageable pageable);

    // Lọc shipper theo ví điện tử
    List<Shipper> findByEWalletBetween(BigDecimal minWallet, BigDecimal maxWallet);
    Page<Shipper> findByEWalletBetween(BigDecimal minWallet, BigDecimal maxWallet, Pageable pageable);

    // Kết hợp nhiều điều kiện: trạng thái + rating + số đơn
    List<Shipper> findByIsActiveAndRatingBetweenAndTotalDeliveryBetween(
            Boolean isActive, BigDecimal minRating, BigDecimal maxRating, Integer minDelivery, Integer maxDelivery);

    Page<Shipper> findByIsActiveAndRatingBetweenAndTotalDeliveryBetween(
            Boolean isActive, BigDecimal minRating, BigDecimal maxRating, Integer minDelivery, Integer maxDelivery, Pageable pageable);

    // Lọc theo tên chứa keyword + loại xe + trạng thái hoạt động
    List<Shipper> findByFullNameContainingIgnoreCaseAndVehicleTypeAndIsActive(
            String keyword, String vehicleType, Boolean isActive);

    Page<Shipper> findByFullNameContainingIgnoreCaseAndVehicleTypeAndIsActive(
            String keyword, String vehicleType, Boolean isActive, Pageable pageable);

    // Lọc theo delivery + trạng thái + rating
    List<Shipper> findByDeliveryAndIsActiveAndRatingBetween(
            Delivery delivery, Boolean isActive, BigDecimal minRating, BigDecimal maxRating);

    Page<Shipper> findByDeliveryAndIsActiveAndRatingBetween(
            Delivery delivery, Boolean isActive, BigDecimal minRating, BigDecimal maxRating, Pageable pageable);
}