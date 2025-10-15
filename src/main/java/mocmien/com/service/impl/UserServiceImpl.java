package mocmien.com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.entity.Role;
import mocmien.com.entity.User;
import mocmien.com.enums.RoleName;
import mocmien.com.enums.UserStatus;
import mocmien.com.repository.RoleRepository;
import mocmien.com.repository.UserRepository;
import mocmien.com.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository=roleRepository;
		this.passwordEncoder=passwordEncoder;
	}

	// -----------------------
	// Tìm kiếm
	// -----------------------
	@Override
	public Optional<User> findById(Integer id) {
		return userRepository.findById(id);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public Optional<User> findByEmailAndStatus(String email, UserStatus status) {
		return userRepository.findByEmailAndStatus(email, status.getValue());
	}

	@Override
	public Optional<User> findByUsernameAndStatus(String username, UserStatus status) {
		return userRepository.findByUsernameAndStatus(username, status.getValue());
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public List<User> findByStatus(UserStatus status) {
		return userRepository.findByStatus(status.getValue());
	}

	@Override
	public List<User> findByRole(Role role) {
		return userRepository.findByRole(role);
	}

	@Override
	public List<User> findByRoleAndStatus(Role role, UserStatus status) {
		return userRepository.findByRoleAndStatus(role, status.getValue());
	}

	// -----------------------
	// Kiểm tra tồn tại
	// -----------------------
	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public boolean existsByEmailAndStatus(String email, UserStatus status) {
		return userRepository.existsByEmailAndStatus(email, status.getValue());
	}

	@Override
	public boolean existsByUsernameAndStatus(String username, UserStatus status) {
		return userRepository.existsByUsernameAndStatus(username, status.getValue());
	}

	// -----------------------
	// Đếm
	// -----------------------
	@Override
	public long countByStatus(UserStatus status) {
		return userRepository.countByStatus(status.getValue());
	}

	@Override
	public long countByRole(Role role) {
		return userRepository.countByRole(role);
	}

	@Override
	public long countByRoleAndStatus(Role role, UserStatus status) {
		return userRepository.countByRoleAndStatus(role, status.getValue());
	}

	@Override
	public long countAllUsers() {
		return userRepository.count();
	}

	// -----------------------
	// Đăng ký User mới
	// -----------------------
	@Override
	public User registerUser(User user, String roleName) {

		if (existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email đã tồn tại");
		}

		if (existsByUsername(user.getUsername())) {
			throw new RuntimeException("Username đã tồn tại");
		}

		// Hash password
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Gán role
		Role role = roleRepository.findByRoleName(RoleName.valueOf(roleName))
				.orElseThrow(() -> new RuntimeException("Role không tồn tại"));
		user.setRole(role);

		// Trạng thái ACTIVE mặc định
		user.setStatus(UserStatus.ACTIVE.getValue());

		// Avatar mặc định
		if (user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
			user.setImageUrl("profile/customer/default.png");
		}

		return userRepository.save(user);
	}

	// -----------------------
	// Đăng nhập
	// -----------------------
	@Override
	public Optional<User> login(String usernameOrEmail, String password) {
		Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail)
				.or(() -> userRepository.findByEmail(usernameOrEmail));

		if (userOpt.isEmpty()) {
			System.out.println("[LOGIN] Không tìm thấy user: " + usernameOrEmail);
			return Optional.empty();
		}

		User user = userOpt.get();

		boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
		UserStatus status = UserStatus.fromValue(user.getStatus());

		System.out.printf("[LOGIN] User: %s | Role: %s | Status: %s | Password match: %s%n", user.getUsername(),
				user.getRole().getRoleName(), status, passwordMatch);
//
//		// Kiểm tra trạng thái
//		if (!status.canLogin()) {
//			System.out.println("[LOGIN] User không được phép đăng nhập (status = " + status + ")");
//			return Optional.empty();
//		}
//
//		// Kiểm tra mật khẩu
//		if (!passwordMatch) {
//			System.out.println("[LOGIN] Mật khẩu không đúng");
//			return Optional.empty();
//		}

		return Optional.of(user);
	}

	// -----------------------
	// Cập nhật User
	// -----------------------
	@Override
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	// -----------------------
	// Đổi mật khẩu
	// -----------------------
	@Override
	public void changePassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	// -----------------------
	// Thay đổi trạng thái
	// -----------------------
	@Override
	public void setStatus(User user, UserStatus status) {
		user.setStatus(status.getValue());
		userRepository.save(user);
	}

	// -----------------------
	// Xóa User
	// -----------------------
	@Override
	public void deleteUser(User user) {
		userRepository.delete(user);
	}

	// -----------------------
	// Tìm kiếm nâng cao
	// -----------------------
	@Override
	public List<User> searchByUsername(String keyword) {
		return userRepository.findByUsernameContainingIgnoreCase(keyword);
	}

	@Override
	public List<User> findTop10ByStatusOrderByCreatedAtDesc(UserStatus status) {
		return userRepository.findTop10ByStatusOrderByCreatedAtDesc(status.getValue());
	}
	
	@Override
	public Page<User> findByUsernameContainingAndEmailContaining(String username, String email, Pageable pageable){
		return userRepository.findByUsernameContainingAndEmailContaining(username, email, pageable);
	}


}