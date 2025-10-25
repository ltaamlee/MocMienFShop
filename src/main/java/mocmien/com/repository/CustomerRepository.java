package mocmien.com.repository;

import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import mocmien.com.entity.Customer;
import mocmien.com.entity.Level;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	// =====================================================
	// 🧩 CRUD cơ bản
	// =====================================================
	Customer save(Customer customer);
	Customer update(Customer customer);

	void deleteById(Integer id);
	void deleteByUser_UserId(Integer userId);
	void deleteByIdCard(String idCard);

	// =====================================================
	// TÌM KIẾM & LỌC DỮ LIỆU
	// =====================================================
	@Query("SELECT c FROM Customer c WHERE c.user.userId = :userId")
	Optional<Customer> findByUserId(@Param("userId") Integer userId);

	Optional<Customer> findByIdCard(String idCard);
	List<Customer> findByLevel(Level level);
	List<Customer> findByFullNameContainingIgnoreCase(String fullName);
	List<Customer> findByDob(LocalDate dob);
	List<Customer> findByPointBetween(Integer min, Integer max);
	List<Customer> findByEWalletGreaterThan(BigDecimal amount);

	@Query("SELECT c FROM Customer c WHERE c.level.name = :rankName")
	List<Customer> findByRankName(@Param("rankName") String rankName);

	// =====================================================
	// CẬP NHẬT DỮ LIỆU
	// =====================================================
	@Transactional
	@Modifying
	@Query("UPDATE Customer c SET c.point = :point WHERE c.id = :customerId")
	void updatePoint(@Param("customerId") Integer customerId, @Param("point") Integer point);

	@Transactional
	@Modifying
	@Query("UPDATE Customer c SET c.eWallet = :amount WHERE c.id = :customerId")
	void updateEWallet(@Param("customerId") Integer customerId, @Param("amount") BigDecimal amount);

	@Transactional
	@Modifying
	@Query("UPDATE Customer c SET c.level = :level WHERE c.id = :customerId")
	void updateCustomerLevel(@Param("customerId") Integer customerId, @Param("level") Level level);

	// =====================================================
	// KIỂM TRA TỒN TẠI
	// =====================================================
	boolean existsByIdCard(String idCard);
	boolean existsByUser_UserId(Integer userId);
	boolean existsByFullNameAndDob(String fullName, LocalDate dob);

	// =====================================================
	// THỐNG KÊ & ĐẾM
	// =====================================================
	long countByLevel(Level level);
	long countByPointBetween(Integer min, Integer max);

	// =====================================================
	// ÌM KIẾM NÂNG CAO / PHÂN TRANG
	// =====================================================
	Page<Customer> findByLevel(Level level, Pageable pageable);
	Page<Customer> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);
	Page<Customer> findByPointBetween(Integer min, Integer max, Pageable pageable);

	// =====================================================
	// 🌟 CUSTOM QUERIES KHÁC 
	// =====================================================
	List<Customer> findTopCustomersByPoint();

	@Query("SELECT c FROM Customer c ORDER BY c.eWallet DESC")
	List<Customer> findTopCustomersByEWallet();

}