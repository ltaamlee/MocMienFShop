package mocmien.com.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import mocmien.com.dto.response.users.UserResponse;
import mocmien.com.dto.response.users.UserStats;
import mocmien.com.entity.Level;
import mocmien.com.entity.Role;
import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;
import mocmien.com.enums.Rank;
import mocmien.com.enums.RoleName;
import mocmien.com.enums.UserStatus;
import mocmien.com.repository.LevelRepository;
import mocmien.com.repository.RoleRepository;
import mocmien.com.repository.UserProfileRepository;
import mocmien.com.repository.UserRepository;
import mocmien.com.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserProfileRepository customerRepository;
	@Autowired
	private LevelRepository levelRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User save(User user) {

		return userRepository.save(user);
	}

	// LOGIN VỚI GOOGLE
	@Override
	public User createOAuthUser(String email, String fullName) {
		// Kiểm tra user đã tồn tại
		Optional<User> existingUserOpt = userRepository.findByEmail(email);
		if (existingUserOpt.isPresent()) {
			User existingUser = existingUserOpt.get();
			existingUser.setStatus(UserStatus.ONLINE);
			existingUser.setLastLoginAt(LocalDateTime.now());
			return userRepository.save(existingUser);
		}

		// Tạo user mới
		User user = new User();
		user.setEmail(email);
		user.setUsername(email); // dùng email làm username để JWT khớp

		user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
		user.setActive(true);
		user.setStatus(UserStatus.ONLINE);
		user.setLastLoginAt(LocalDateTime.now());

		Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER)
				.orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại"));
		user.setRole(customerRole);

		User savedUser = userRepository.save(user);

		// Tạo Customer liên kết
		UserProfile customer = new UserProfile();
		customer.setUser(savedUser);
		customer.setFullName(fullName);

		Level basicLevel = levelRepository.findByName(Rank.NEW)
				.orElseThrow(() -> new RuntimeException("Level NEW không tồn tại"));
		customer.setLevel(basicLevel);
		customerRepository.save(customer);

		return savedUser;
	}

	@Override
	public Optional<User> login(String usernameOrEmail, String password) {
		// Tìm user theo username hoặc email
		Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
		if (userOpt.isEmpty()) {
			userOpt = userRepository.findByEmail(usernameOrEmail);
		}

		// Không tìm thấy user
		if (userOpt.isEmpty()) {
			throw new RuntimeException("Tài khoản không tồn tại!");
		}

		User user = userOpt.get();

		// Kiểm tra mật khẩu
		boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
		UserStatus status = user.getStatus();

		System.out.printf("[LOGIN] User: %s | Role: %s | Status: %s | Password match: %s%n", user.getUsername(),
				user.getRole().getRoleName(), status, passwordMatch);

		// Kiểm tra trạng thái hoạt động
		if (!user.isActive()) {
			throw new RuntimeException("Tài khoản của bạn đang bị khóa hoặc ngừng hoạt động!");
		}

		if (!passwordMatch) {
			throw new RuntimeException("Mật khẩu không chính xác!");
		}

		return Optional.of(user);
	}

	@Override
	public Optional<User> register(User user, RoleName roleName, String fullName) {

		// ✅ Validate user object trước
		if (user == null) {
			throw new RuntimeException("Dữ liệu người dùng không hợp lệ!");
		}
		
		if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			throw new RuntimeException("Email không được để trống!");
		}
		
		if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
			throw new RuntimeException("Username không được để trống!");
		}
		
		if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			throw new RuntimeException("Mật khẩu không được để trống!");
		}
		
		if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
			throw new RuntimeException("Số điện thoại không được để trống!");
		}
		
		if (fullName == null || fullName.trim().isEmpty()) {
			throw new RuntimeException("Họ tên không được để trống!");
		}

		// Kiểm tra tồn tại
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email đã tồn tại!");
		}

		if (userRepository.existsByUsername(user.getUsername())) {
			throw new RuntimeException("Username đã tồn tại!");
		}

		if (userRepository.existsByPhone(user.getPhone())) {
			throw new RuntimeException("Số điện thoại đã tồn tại!");
		}

		// Hash password
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Gán role mặc định CUSTOMER
		Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER)
				.orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại"));
		user.setRole(customerRole);

		// Trạng thái mặc định
		user.setActive(true);
		user.setStatus(UserStatus.OFFLINE);

		// Avatar mặc định
		if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
			user.setAvatar("profile/customer/default.png");
		}

		// Lưu User trước
		User savedUser = userRepository.save(user);

		// Lưu Customer, liên kết user
		UserProfile customer = new UserProfile();
		customer.setUser(savedUser);
		customer.setFullName(fullName);

		Level basicLevel = levelRepository.findByName(Rank.NEW)
				.orElseThrow(() -> new RuntimeException("Level NEW không tồn tại"));
		customer.setLevel(basicLevel);
		customerRepository.save(customer);

		return Optional.of(savedUser);
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

	}

	@Override
	public void changeBlock(Integer userId) {
		userRepository.findById(userId).ifPresent(user -> {

			// 1. Lấy trạng thái ngược lại (Chuyển đổi trạng thái)
			boolean newStatus = !user.isActive();

			// 2. Cập nhật trạng thái
			user.setActive(newStatus);
			userRepository.save(user);

			// 3. Log hành động (để tiện kiểm tra)
			String action = newStatus ? "UNBLOCKED (Active=true)" : "BLOCKED (Active=false)";
			System.out.println("[USER STATUS TOGGLED] " + user.getUsername() + " -> " + action);
		});
	}

	@Override
	public void setStatus(Integer userId, UserStatus status) {
		userRepository.findById(userId).ifPresent(user -> {
			user.setStatus(status);
			userRepository.save(user);
			System.out.println("[USER STATUS] " + user.getUsername() + " → " + status);
		});
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
	public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
		// Thử tìm theo username
		Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);

		// Nếu không có, thử tìm theo email
		if (userOpt.isEmpty()) {
			userOpt = userRepository.findByEmail(usernameOrEmail);
		}

		return userOpt;
	}

	@Override
	public UserStats getUserStatistics() {
		long total = userRepository.count(); // tổng số user
		long active = userRepository.countByStatus(UserStatus.ONLINE); // online
		long inactive = userRepository.countByStatus(UserStatus.OFFLINE); // offline
		long blocked = userRepository.countByIsActiveFalse();

		return new UserStats(total, active, inactive, blocked);
	}

	@Override
	public List<User> searchByUsername(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<UserResponse> findAll(String keyword, UserStatus status, Boolean isActive, String roleName,
			Pageable pageable) {

		// Chuyển roleName string sang enum
		RoleName roleEnum = null;
		if (roleName != null && !roleName.isEmpty()) {
			try {
				roleEnum = RoleName.valueOf(roleName.toUpperCase());
			} catch (IllegalArgumentException e) {
				roleEnum = null;
			}
		}
		System.out.println("RoleName from request: " + roleName);

		// Chuẩn hóa keyword
		String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim().toLowerCase() : null;

		// Lấy dữ liệu phân trang trực tiếp từ repository
		Page<User> users = userRepository.searchUsers(kw, status, isActive, roleEnum, pageable);

		// Map entity → DTO
		return users.map(user -> new UserResponse(user.getUserId(), user.getUsername(),
				user.getUserProfile() != null ? user.getUserProfile().getFullName() : null, user.getEmail(),
				user.getPhone(), user.getRole() != null ? user.getRole().getRoleName().name() : null, user.getStatus(),
				user.isActive(), user.getCreatedAt(), user.getLastLoginAt()));
	}

	@Override
	public void deleteById(Integer userId) {
		userRepository.deleteById(userId);
	}

	@Override
	public Optional<User> findById(Integer userId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public boolean existsById(Integer userId) {
		// TODO Auto-generated method stub
		return false;
	}

}
