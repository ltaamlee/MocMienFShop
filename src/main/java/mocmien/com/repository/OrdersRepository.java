package mocmien.com.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Customer;
import mocmien.com.entity.Orders;
import mocmien.com.entity.Shipper;
import mocmien.com.entity.Store;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {

    // -----------------------
    // CRUD cơ bản (JpaRepository đã có sẵn)
    // -----------------------
    // save, findById, findAll, deleteById, etc.

    // -----------------------
    // Tìm kiếm theo customer
    // -----------------------
    List<Orders> findByCustomer(Customer customer);
    List<Orders> findByCustomerAndStatus(Customer customer, String status);
    Page<Orders> findByCustomer(Customer customer, Pageable pageable);
    Page<Orders> findByCustomerAndStatus(Customer customer, String status, Pageable pageable);

    // -----------------------
    // Tìm kiếm theo store
    // -----------------------
    List<Orders> findByStore(Store store);
    List<Orders> findByStoreAndStatus(Store store, String status);
    Page<Orders> findByStore(Store store, Pageable pageable);
    Page<Orders> findByStoreAndStatus(Store store, String status, Pageable pageable);

    // -----------------------
    // Tìm kiếm theo shipper
    // -----------------------
    List<Orders> findByShipper(Shipper shipper);
    List<Orders> findByShipperAndStatus(Shipper shipper, String status);
    Page<Orders> findByShipper(Shipper shipper, Pageable pageable);
    Page<Orders> findByShipperAndStatus(Shipper shipper, String status, Pageable pageable);

    // -----------------------
    // Tìm kiếm theo trạng thái
    // -----------------------
    List<Orders> findByStatus(String status);
    Page<Orders> findByStatus(String status, Pageable pageable);

    // -----------------------
    // Tìm kiếm theo khoảng thời gian
    // -----------------------
    List<Orders> findByCreateAtBetween(LocalDateTime start, LocalDateTime end);
    List<Orders> findByUpdateAtBetween(LocalDateTime start, LocalDateTime end);

    // -----------------------
    // Thống kê số lượng đơn
    // -----------------------
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.customer = :customer")
    long countByCustomer(@Param("customer") Customer customer);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.store = :store")
    long countByStore(@Param("store") Store store);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.shipper = :shipper")
    long countByShipper(@Param("shipper") Shipper shipper);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = :status")
    long countByStatus(@Param("status") String status);

    // -----------------------
    // Thống kê tổng giá trị đơn hàng
    // -----------------------
    @Query("SELECT SUM(o.amountFromCustomer) FROM Orders o WHERE o.customer = :customer")
    BigDecimal sumAmountByCustomer(@Param("customer") Customer customer);

    @Query("SELECT SUM(o.amountToStore) FROM Orders o WHERE o.store = :store")
    BigDecimal sumAmountToStoreByStore(@Param("store") Store store);

    @Query("SELECT SUM(o.amountToSys) FROM Orders o WHERE o.store = :store")
    BigDecimal sumAmountToSysByStore(@Param("store") Store store);

    @Query("SELECT SUM(o.amountFromCustomer) FROM Orders o WHERE o.shipper = :shipper")
    BigDecimal sumAmountByShipper(@Param("shipper") Shipper shipper);

    @Query("SELECT SUM(o.amountFromCustomer) FROM Orders o WHERE o.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") String status);

    // -----------------------
    // Thống kê theo khoảng thời gian
    // -----------------------
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.createAt BETWEEN :start AND :end")
    long countOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.amountFromCustomer) FROM Orders o WHERE o.createAt BETWEEN :start AND :end")
    BigDecimal sumAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // -----------------------
    // Thống kê tổng hợp: theo store + trạng thái
    // -----------------------
    @Query("SELECT COUNT(o), SUM(o.amountFromCustomer) FROM Orders o WHERE o.store = :store AND o.status = :status")
    Object[] countAndSumByStoreAndStatus(@Param("store") Store store, @Param("status") String status);

    // -----------------------
    // Thống kê tổng hợp: theo shipper + trạng thái
    // -----------------------
    @Query("SELECT COUNT(o), SUM(o.amountFromCustomer) FROM Orders o WHERE o.shipper = :shipper AND o.status = :status")
    Object[] countAndSumByShipperAndStatus(@Param("shipper") Shipper shipper, @Param("status") String status);

    // -----------------------
    // Lấy các đơn hàng chưa phân công shipper
    // -----------------------
    List<Orders> findByShipperIsNull();

}