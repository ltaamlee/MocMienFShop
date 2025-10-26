package mocmien.com.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

    // -----------------------
    // CRUD cơ bản (JpaRepository đã có sẵn)
    // -----------------------
    // save, findById, findAll, deleteById, ...

    // -----------------------
    // Tìm kiếm theo tên
    // -----------------------
    List<Delivery> findByDeliveryNameContainingIgnoreCase(String keyword);
    Optional<Delivery> findByDeliveryName(String deliveryName);
    Page<Delivery> findByDeliveryNameContainingIgnoreCase(String keyword, Pageable pageable);

    // -----------------------
    // Lọc theo trạng thái hoạt động
    // -----------------------
    List<Delivery> findByIsActive(Boolean isActive);
    Page<Delivery> findByIsActive(Boolean isActive, Pageable pageable);

    // -----------------------
    // Sắp xếp theo giá
    // -----------------------
    List<Delivery> findAllByOrderByBasePriceAsc();
    List<Delivery> findAllByOrderByBasePriceDesc();
    List<Delivery> findAllByOrderByPricePerKMAsc();
    List<Delivery> findAllByOrderByPricePerKMDesc();

    // -----------------------
    // Thống kê tổng số đơn vị giao hàng
    // -----------------------
    @Query("SELECT COUNT(d) FROM Delivery d")
    long countAllDeliveries();

    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.isActive = true")
    long countActiveDeliveries();

    // -----------------------
    // Thống kê khoảng giá
    // -----------------------
    @Query("SELECT MIN(d.basePrice) FROM Delivery d")
    BigDecimal minBasePrice();

    @Query("SELECT MAX(d.basePrice) FROM Delivery d")
    BigDecimal maxBasePrice();

    @Query("SELECT AVG(d.basePrice) FROM Delivery d")
    BigDecimal avgBasePrice();

    @Query("SELECT MIN(d.pricePerKM) FROM Delivery d")
    BigDecimal minPricePerKM();

    @Query("SELECT MAX(d.pricePerKM) FROM Delivery d")
    BigDecimal maxPricePerKM();

    @Query("SELECT AVG(d.pricePerKM) FROM Delivery d")
    BigDecimal avgPricePerKM();
}