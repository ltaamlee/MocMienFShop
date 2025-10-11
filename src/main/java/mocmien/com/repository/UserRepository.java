package mocmien.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Role;
import mocmien.com.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	// -----------------------
	// Tìm kiếm User
	// -----------------------
	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	Optional<User> findByEmailAndStatus(String email, Integer status);

	Optional<User> findByUsernameAndStatus(String username, Integer status);

	List<User> findByStatus(Integer status);

	List<User> findByRole(Role role);

	List<User> findByRoleAndStatus(Role role, Integer status);

	// -----------------------
	// Kiểm tra tồn tại
	// -----------------------
	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmailAndStatus(String email, Integer status);

	boolean existsByUsernameAndStatus(String username, Integer status);

	// -----------------------
	// Đếm số lượng
	// -----------------------
	long countByStatus(Integer status);

	long countByRole(Role role);

	long countByRoleAndStatus(Role role, Integer status);

	// -----------------------
	// Xóa
	// -----------------------
	void deleteByEmail(String email);

	void deleteByUsername(String username);

	// -----------------------
	// Optional nâng cao
	// -----------------------
	List<User> findTop10ByStatusOrderByCreatedAtDesc(Integer status); // 10 user mới nhất theo status

	List<User> findByUsernameContainingIgnoreCase(String keyword); // search username

	@Query(value = """
			SELECT u FROM User u
			LEFT JOIN FETCH u.role
			LEFT JOIN FETCH u.khachHang
			LEFT JOIN FETCH u.nhanVien
			WHERE (:keyword IS NULL OR :keyword = '' OR
			       LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
			       LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
			  AND (:status IS NULL OR :status = '' OR CAST(u.status AS string) = :status)
			  AND (:roleId IS NULL OR u.role.roleId = :roleId)
			""", countQuery = """
			SELECT count(u) FROM User u
			WHERE (:keyword IS NULL OR :keyword = '' OR
			       LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
			       LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
			  AND (:status IS NULL OR :status = '' OR CAST(u.status AS string) = :status)
			  AND (:roleId IS NULL OR u.role.roleId = :roleId)
			""")
	Page<User> searchUsers(@Param("keyword") String keyword, @Param("status") String status,
			@Param("roleId") Integer roleId, Pageable pageable);
}
