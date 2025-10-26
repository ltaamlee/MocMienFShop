package mocmien.com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mocmien.com.entity.Role;
import mocmien.com.entity.User;
import mocmien.com.enums.RoleName;
import mocmien.com.enums.UserStatus;


public interface UserService {

	// -----------------------
	// CRUD
	// -----------------------
    User save(User user);

    Optional<User> login(String usenameOrEmail, String password);
    Optional<User> register(User user, RoleName roleName, String fullName);
    User createOAuthUser(String email, String fullName);
    
    void deleteByEmail(String email);
	void deleteByUsername(String username);
	void deleteByPhone(String phone);
	
	// -----------------------
    // Thay đổi trạng thái / active
    // -----------------------
    void blockUser(Integer userId);
    void unblockUser(Integer userId);
    void setStatus(Integer userId, UserStatus status);
	
	// -----------------------
	// Tìm kiếm User
	// -----------------------
	Optional<User> findByEmail(String email);
	Optional<User> findByUsername(String username);
	Optional<User> findByEmailAndStatus(String email, UserStatus status);
	Optional<User> findByUsernameAndStatus(String username, UserStatus status);
	Optional<User> findByUsernameOrEmail (String usernameOrEmail);
	
	
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


	//Tìm kiếm phân trang
	Page<User> findByStatus(UserStatus status, Pageable pageable);
	Page<User> findByUsernameContainingIgnoreCase(String keyword, Pageable pageable);
    
}
