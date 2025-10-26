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

	// -----------------------
	// Optional nâng cao
	// -----------------------
	List<User> findTop10ByStatusOrderByCreatedAtDesc(UserStatus status); // 10 user mới nhất theo status
	List<User> findByUsernameContainingIgnoreCase(String keyword); // search username
	
	Page<User> findByUsernameContainingAndEmailContaining(String username, String email, Pageable pageable);
	//Tìm kiếm phân trang
	Page<User> findByStatus(UserStatus status, Pageable pageable);
	Page<User> findByUsernameContainingIgnoreCase(String keyword, Pageable pageable);

}
