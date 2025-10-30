package mocmien.com.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Orders;
import mocmien.com.entity.UserProfile;
import mocmien.com.enums.OrderStatus;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, String>, JpaSpecificationExecutor<Orders> {

	/////// Dùng cho thống kê
	// ====== Revenue for a single Store (Vendor) ======
	@Query("""
			   SELECT COALESCE(SUM(o.amountFromCustomer), 0)
			   FROM Orders o
			   WHERE o.store.id = :storeId
			     AND o.status = :status
			     AND o.createdAt >= :start AND o.createdAt < :end
			""")
	BigDecimal sumRevenueForStoreAndRange(@Param("storeId") Integer storeId, @Param("status") OrderStatus status,
			@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			   SELECT COUNT(o)
			   FROM Orders o
			   WHERE o.store.id = :storeId
			     AND o.status = :status
			     AND o.createdAt >= :start AND o.createdAt < :end
			""")
	long countDeliveredOrdersForStoreAndRange(@Param("storeId") Integer storeId, @Param("status") OrderStatus status,
			@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			   SELECT COALESCE(SUM(o.amountToSys), 0)
			   FROM Orders o
			   WHERE o.store.id = :storeId
			     AND o.status = :status
			     AND o.createdAt >= :start AND o.createdAt < :end
			""")
	BigDecimal sumPlatformFeeForStoreAndRange(@Param("storeId") Integer storeId, @Param("status") OrderStatus status,
			@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			   SELECT COALESCE(SUM(o.amountToStore), 0)
			   FROM Orders o
			   WHERE o.store.id = :storeId
			     AND o.status = :status
			     AND o.createdAt >= :start AND o.createdAt < :end
			""")
	BigDecimal sumNetToStoreForStoreAndRange(@Param("storeId") Integer storeId, @Param("status") OrderStatus status,
			@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	// ====== Chart points (Daily) ======
	@Query("""
			   SELECT YEAR(o.createdAt) AS orderYear,
			          MONTH(o.createdAt) AS orderMonth,
			          DAY(o.createdAt)   AS orderDay,
			          SUM(o.amountFromCustomer) AS total
			   FROM Orders o
			   WHERE o.store.id = :storeId
			     AND o.status = :status
			     AND o.createdAt >= :startDate AND o.createdAt < :endDate
			   GROUP BY YEAR(o.createdAt), MONTH(o.createdAt), DAY(o.createdAt)
			   ORDER BY orderYear, orderMonth, orderDay
			""")
	List<Object[]> findDailyRevenueByStore(@Param("storeId") Integer storeId, @Param("status") OrderStatus status,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	// ====== Chart points (Monthly) ======
	@Query("""
			   SELECT YEAR(o.createdAt) AS orderYear,
			          MONTH(o.createdAt) AS orderMonth,
			          SUM(o.amountFromCustomer) AS total
			   FROM Orders o
			   WHERE o.store.id = :storeId
			     AND o.status = :status
			     AND o.createdAt >= :startDate AND o.createdAt < :endDate
			   GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
			   ORDER BY orderYear, orderMonth
			""")
	List<Object[]> findMonthlyRevenueByStore(@Param("storeId") Integer storeId, @Param("status") OrderStatus status,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	// ====== Recent orders of the Store ======
	@Query("""
			   SELECT o FROM Orders o
			   WHERE o.store.id = :storeId
			   ORDER BY o.createdAt DESC
			""")
	List<Orders> findRecentOrdersByStore(@Param("storeId") Integer storeId, Pageable pageable);

	@Query("""
			   SELECT COALESCE(SUM(o.amountFromCustomer),0),
			          COALESCE(SUM(o.amountToSys),0),
			          COALESCE(SUM(o.amountToStore),0),
			          COUNT(o)
			   FROM Orders o
			   WHERE o.store.id = :storeId
			     AND o.status = :status
			     AND o.createdAt >= :startDate AND o.createdAt < :endDate
			""")
	Object[] sumForStoreBetween(@Param("storeId") Integer storeId, @Param("status") OrderStatus status,
			@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

	@Query("""
			   SELECT YEAR(o.createdAt) as y, MONTH(o.createdAt) as m, DAY(o.createdAt) as d,
			          SUM(o.amountFromCustomer) as total
			   FROM Orders o
			   WHERE o.store.id = :storeId
			     AND o.status = :status
			     AND o.createdAt >= :startDate AND o.createdAt < :endDate
			   GROUP BY YEAR(o.createdAt), MONTH(o.createdAt), DAY(o.createdAt)
			   ORDER BY y,m,d
			""")
	List<Object[]> findDailyRevenueOfStore(@Param("storeId") Integer storeId, @Param("status") OrderStatus status,
			@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

	@Query("""
			   SELECT YEAR(o.createdAt) as y, MONTH(o.createdAt) as m,
			          SUM(o.amountFromCustomer) as total
			   FROM Orders o
			   WHERE o.store.id = :storeId
			     AND o.status = :status
			     AND o.createdAt >= :startDate AND o.createdAt < :endDate
			   GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
			   ORDER BY y,m
			""")
	List<Object[]> findMonthlyRevenueOfStore(@Param("storeId") Integer storeId, @Param("status") OrderStatus status,
			@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

	// Bảng đơn đã giao (table)
	Page<Orders> findByStore_IdAndStatusAndCreatedAtBetweenOrderByCreatedAtDesc(Integer storeId, OrderStatus status,
			java.time.LocalDateTime start, java.time.LocalDateTime end, Pageable pageable);

	//////////////
	Page<Orders> findByStore_Id(Integer storeId, Pageable pageable);

	Page<Orders> findByStore_IdAndStatusIn(Integer storeId, List<OrderStatus> statuses, Pageable pageable);

	@EntityGraph(attributePaths = { "customer", "store" })
	Page<Orders> findByStore_IdOrderByCreatedAtDesc(Integer storeId, Pageable pageable);

	@EntityGraph(attributePaths = { "customer", "store" })
	Page<Orders> findByStore_IdAndStatusInOrderByCreatedAtDesc(Integer storeId, List<OrderStatus> statuses,
			Pageable pageable);

	@EntityGraph(attributePaths = { "customer", "store" })
	Optional<Orders> findByIdAndStore_Id(String id, Integer storeId);

	// Shipper queries
	List<Orders> findByDelivery_IdAndStatusAndShipperIsNull(Integer deliveryId, OrderStatus status);

	List<Orders> findByShipper_IdAndStatusIn(Integer shipperId, List<OrderStatus> statuses);

	Optional<Orders> findByIdAndShipper_Id(String id, Integer shipperId);

    // Admin total revenue = fixed 10% commission of gross (amountFromCustomer) for delivered orders
    @Query("SELECT COALESCE(SUM(o.amountFromCustomer) * 0.1, 0) FROM Orders o WHERE o.status = 'DELIVERED'")
    Optional<BigDecimal> getAdminTotalRevenue(OrderStatus delivered);

	@Query("SELECT COUNT(o) FROM Orders o WHERE o.status = :status AND o.createdAt >= :sinceDate")
	long countCompletedOrdersSince(@Param("status") OrderStatus status, @Param("sinceDate") LocalDateTime sinceDate);

	@Query("SELECT o FROM Orders o ORDER BY o.createdAt DESC")
	List<Orders> findRecentOrders(Pageable pageable);

    // Chart (daily): admin revenue = 10% of amountFromCustomer
    @Query("SELECT YEAR(o.createdAt) as orderYear, " + "       MONTH(o.createdAt) as orderMonth, "
            + "       DAY(o.createdAt) as orderDay, " + "       SUM(o.amountFromCustomer) * 0.1 as total " + "FROM Orders o "
			+ "WHERE o.status = :status AND o.createdAt >= :startDate AND o.createdAt < :endDate "
			+ "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt), DAY(o.createdAt) "
			+ "ORDER BY orderYear ASC, orderMonth ASC, orderDay ASC")
	List<Object[]> findDailyRevenue(@Param("status") OrderStatus status, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

    // Chart (monthly): admin revenue = 10% of amountFromCustomer
    @Query("SELECT YEAR(o.createdAt) as orderYear, MONTH(o.createdAt) as orderMonth, SUM(o.amountFromCustomer) * 0.1 as total "
            + "FROM Orders o " + "WHERE o.status = :status AND o.createdAt >= :startDate AND o.createdAt < :endDate "
			+ "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " + "ORDER BY orderYear ASC, orderMonth ASC")
	List<Object[]> findMonthlyRevenue(@Param("status") OrderStatus status, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	List<Orders> findByCustomer(UserProfile customer);

	List<Orders> findByCustomerAndStatus(UserProfile customer, OrderStatus status);

	List<Orders> findByCustomerOrderByCreatedAtDesc(UserProfile customer);
	List<Orders> findByCustomerAndStatusOrderByCreatedAtDesc(UserProfile customer, OrderStatus status);
}