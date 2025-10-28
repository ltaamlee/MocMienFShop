package mocmien.com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mocmien.com.dto.response.users.UserResponse;
import mocmien.com.dto.response.users.UserStats;
import mocmien.com.entity.User;
import mocmien.com.enums.RoleName;
import mocmien.com.enums.UserStatus;


public interface UserService {

	// -----------------------
	// CRUD
	// -----------------------
    User save(User user);

    Optional<User> login(String usernameOrEmail, String password);
    Optional<User> register(User user, RoleName roleName, String fullName);
    User createOAuthUser(String email, String fullName);
    
    void deleteById(Integer userId);
    void deleteByEmail(String email);
	void deleteByUsername(String username);
	void deleteByPhone(String phone);
		
	
	
	// -----------------------
    // Thống kê
    // -----------------------
    UserStats getUserStatistics();

    // -----------------------
    // Thay đổi trạng thái / active
    // -----------------------
    void changeBlock(Integer userId);
    void setStatus(Integer userId, UserStatus status);

    // -----------------------
    // Tìm kiếm / phân trang
    // -----------------------
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    List<User> searchByUsername(String keyword);

    Page<UserResponse> findAll(String keyword, UserStatus status, Boolean isActive, String roleName, Pageable pageable);

    
}
