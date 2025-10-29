package mocmien.com.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocmien.com.dto.response.users.UserResponse;
import mocmien.com.entity.Role;
import mocmien.com.entity.User;
import mocmien.com.enums.RoleName;
import mocmien.com.enums.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	// -----------------------
	User save(User user);

	void deleteByEmail(String email);

	void deleteByUsername(String username);

	void deleteByPhone(String phone);

	// -----------------------
	// Tìm kiếm User
	// -----------------------
	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	Optional<User> findByEmailAndStatus(String email, UserStatus status);

	Optional<User> findByUsernameAndStatus(String username, UserStatus status);

	List<User> findByStatus(UserStatus status);

	List<User> findByRole(Role role);

	List<User> findByRoleAndStatus(Role role, UserStatus status);

	List<User> searchByUsername(String keyword);

	List<User> findByIsActiveFalse();

	// -----------------------
	// Kiểm tra tồn tại
	// -----------------------
	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	boolean existsByEmailAndStatus(String email, UserStatus status);

	boolean existsByUsernameAndStatus(String username, UserStatus status);

	// -----------------------
	// Đếm số lượng
	// -----------------------
	long countByStatus(UserStatus status);

	long countByRole(Role role);

	long countByRoleAndStatus(Role role, UserStatus status);

	long countByIsActiveFalse();

	// -----------------------
	// Optional nâng cao
	// -----------------------
	List<User> findTop10ByStatusOrderByCreatedAtDesc(UserStatus status); // 10 user mới nhất theo status

	List<User> findByUsernameContainingIgnoreCase(String keyword); // search username

	// 1. Tìm theo trạng thái
	Page<User> findByStatus(UserStatus status, Pageable pageable);

	// 2. Tìm theo username hoặc email
	Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email,
			Pageable pageable);

	// 3. Tìm theo cả status + keyword (username/email)
	Page<User> findByStatusAndUsernameContainingIgnoreCaseOrStatusAndEmailContainingIgnoreCase(UserStatus status1,
			String username, UserStatus status2, String email, Pageable pageable);

	@Query("SELECT u FROM User u " + "WHERE (:keyword IS NULL OR LOWER(u.username) LIKE CONCAT('%', :keyword, '%') "
			+ "    OR LOWER(u.email) LIKE CONCAT('%', :keyword, '%') "
			+ "    OR LOWER(u.userProfile.fullName) LIKE CONCAT('%', :keyword, '%')) "
			+ "AND (:status IS NULL OR u.status = :status) " + "AND (:isActive IS NULL OR u.isActive = :isActive) "
			+ "AND (:roleName IS NULL OR (u.role IS NOT NULL AND u.role.roleName = :roleName))")
	Page<User> searchUsers(@Param("keyword") String keyword, @Param("status") UserStatus status,
			@Param("isActive") Boolean isActive, @Param("roleName") RoleName roleName, Pageable pageable);

	@Query("SELECT COUNT(u) FROM User u JOIN u.role r WHERE r.roleName = :roleName AND u.createdAt >= :sinceDate")
	long countNewUsersByRoleSince(@Param("roleName") RoleName roleName, @Param("sinceDate") LocalDateTime sinceDate);

	@Query("SELECT COUNT(u) FROM User u JOIN u.role r WHERE r.roleName = :roleName")
	long countByRole(@Param("roleName") RoleName roleName);
}
