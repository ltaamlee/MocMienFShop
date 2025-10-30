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
import mocmien.com.entity.Shipper;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;
import mocmien.com.enums.OrderStatus;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, String>, JpaSpecificationExecutor<Orders> {
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

	@Query("SELECT SUM(o.amountFromCustomer) FROM Orders o WHERE o.status = 'DELIVERED'")
	Optional<BigDecimal> getAdminTotalRevenue(OrderStatus delivered);

	@Query("SELECT COUNT(o) FROM Orders o WHERE o.status = :status AND o.createdAt >= :sinceDate")
	long countCompletedOrdersSince(@Param("status") OrderStatus status, @Param("sinceDate") LocalDateTime sinceDate);

	@Query("SELECT o FROM Orders o ORDER BY o.createdAt DESC")
	List<Orders> findRecentOrders(Pageable pageable);

	@Query("SELECT YEAR(o.createdAt) as orderYear, " + "       MONTH(o.createdAt) as orderMonth, "
			+ "       DAY(o.createdAt) as orderDay, " + "       SUM(o.amountFromCustomer) as total " + "FROM Orders o "
			+ "WHERE o.status = :status AND o.createdAt >= :startDate AND o.createdAt < :endDate "
			+ "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt), DAY(o.createdAt) "
			+ "ORDER BY orderYear ASC, orderMonth ASC, orderDay ASC")
	List<Object[]> findDailyRevenue(@Param("status") OrderStatus status, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("SELECT YEAR(o.createdAt) as orderYear, MONTH(o.createdAt) as orderMonth, SUM(o.amountFromCustomer) as total "
			+ "FROM Orders o " + "WHERE o.status = :status AND o.createdAt >= :startDate AND o.createdAt < :endDate "
			+ "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " + "ORDER BY orderYear ASC, orderMonth ASC")
	List<Object[]> findMonthlyRevenue(@Param("status") OrderStatus status, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	List<Orders> findByCustomer(UserProfile customer);

	List<Orders> findByCustomerAndStatus(UserProfile customer, OrderStatus status);

	List<Orders> findByCustomerOrderByCreatedAtDesc(UserProfile customer);
	List<Orders> findByCustomerAndStatusOrderByCreatedAtDesc(UserProfile customer, OrderStatus status);
}