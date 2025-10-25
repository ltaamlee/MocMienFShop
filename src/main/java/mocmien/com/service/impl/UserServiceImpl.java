package mocmien.com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import mocmien.com.entity.Role;
import mocmien.com.entity.User;
import mocmien.com.enums.RoleName;
import mocmien.com.enums.UserStatus;
import mocmien.com.repository.UserRepository;
import mocmien.com.service.UserService;

public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository user;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User save(User user) {
		return user.save(user);
	}

	@Override
	public User update(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<User> login(String usernameOrEmail, String password) {
		Optional<User> userOpt = user.findByUsernameOrEmail(usernameOrEmail)
			

		User user = userOpt.get();

		boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
		UserStatus status = UserStatus.fromValue(user.getStatus());

		System.out.printf("[LOGIN] User: %s | Role: %s | Status: %s | Password match: %s%n", user.getUsername(),
				user.getRole().getRoleName(), status, passwordMatch);

		// Kiểm tra trạng thái
		if (!status.canLogin()) {
			System.out.println("[LOGIN] User không được phép đăng nhập (status = " + status + ")");
			return Optional.empty();
		}

		// Kiểm tra mật khẩu
		if (!passwordMatch) {
			System.out.println("[LOGIN] Mật khẩu không đúng");
			return Optional.empty();
		}

		return Optional.of(user);
	}

	@Override
	public Optional<User> register(User user, Role roleName) {

		if (existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email đã tồn tại");
		}

		if (existsByUsername(user.getUsername())) {
			throw new RuntimeException("Username đã tồn tại");
		}

		// Hash password
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Gán role
		Role role = role.findByRoleName(RoleName.valueOf(roleName))
				.orElseThrow(() -> new RuntimeException("Role không tồn tại"));
		user.setRole(role);

		// Trạng thái ACTIVE mặc định
		user.setStatus(UserStatus.ACTIVE.getValue());

		// Avatar mặc định
		if (user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
			user.setImageUrl("profile/customer/default.png");
		}

		return user.save(user);
	}

	@Override
	public void deleteByEmail(String email) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteByUsername(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteByPhone(String phone) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void blockUser(Integer userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unblockUser(Integer userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(Integer userId, UserStatus status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Optional<User> findByEmail(String email) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<User> findByUsername(String username) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<User> findByEmailAndStatus(String email, UserStatus status) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<User> findByUsernameAndStatus(String username, UserStatus status) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<User> findByStatus(UserStatus status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findByRole(Role role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findByRoleAndStatus(Role role, UserStatus status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> searchByUsername(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsByUsername(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsByEmail(String email) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsByPhone(String phone) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsByEmailAndStatus(String email, UserStatus status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsByUsernameAndStatus(String username, UserStatus status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long countByStatus(UserStatus status) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long countByRole(Role role) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long countByRoleAndStatus(Role role, UserStatus status) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<User> findTop10ByStatusOrderByCreatedAtDesc(UserStatus status) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<User> findByUsernameContainingIgnoreCase(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<User> findByStatus(UserStatus status, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<User> findByUsernameContainingIgnoreCase(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

}

