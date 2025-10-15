package mocmien.com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mocmien.com.entity.Role;
import mocmien.com.entity.User;
import mocmien.com.enums.UserStatus;

public interface UserService {

	// Tìm kiếm
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
    Optional<User> findByUsernameAndStatus(String username, UserStatus status);
    List<User> findAll();
    List<User> findByStatus(UserStatus status);
    List<User> findByRole(Role role);
    List<User> findByRoleAndStatus(Role role, UserStatus status);

    // Kiểm tra tồn tại
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmailAndStatus(String email, UserStatus status);
    boolean existsByUsernameAndStatus(String username, UserStatus status);

    // Đếm
    long countByStatus(UserStatus status);
    long countByRole(Role role);
    long countByRoleAndStatus(Role role, UserStatus status);
    long countAllUsers();
    

    // Đăng ký
    User registerUser(User user, String roleName);
    
    //Đăng nhập
    Optional<User> login(String usernameOrEmail, String password);

    // Cập nhật
    User updateUser(User user);

    // Đổi mật khẩu
    void changePassword(User user, String newPassword);

    // Thay đổi trạng thái
    void setStatus(User user, UserStatus status);

    // Xóa
    void deleteUser(User user);

    // Tìm kiếm nâng cao
    List<User> searchByUsername(String keyword);
    List<User> findTop10ByStatusOrderByCreatedAtDesc(UserStatus status);
    
    Page<User> findByUsernameContainingAndEmailContaining(String username, String email, Pageable pageable);

}
