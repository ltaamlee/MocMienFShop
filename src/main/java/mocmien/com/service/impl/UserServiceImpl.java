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

import mocmien.com.entity.Level;
import mocmien.com.entity.Role;
import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;
import mocmien.com.enums.Rank;
import mocmien.com.enums.RoleName;
import mocmien.com.enums.UserStatus;
import mocmien.com.repository.UserProfileRepository;
import mocmien.com.repository.LevelRepository;
import mocmien.com.repository.RoleRepository;
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

	    System.out.printf("[LOGIN] User: %s | Role: %s | Status: %s | Password match: %s%n",
	            user.getUsername(), user.getRole().getRoleName(), status, passwordMatch);

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

		// Kiểm tra tồn tại
		if (existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email đã tồn tại!");
		}

		if (existsByUsername(user.getUsername())) {
			throw new RuntimeException("Username đã tồn tại!");
		}

		if (existsByPhone(user.getPhone())) {
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
	public void blockUser(Integer userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unblockUser(Integer userId) {
		// TODO Auto-generated method stub

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
		// Thử tìm theo username
	    Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
	    
	    // Nếu không có, thử tìm theo email
	    if (userOpt.isEmpty()) {
	        userOpt = userRepository.findByEmail(usernameOrEmail);
	    }
	    
	    return userOpt;
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
		return userRepository.existsByUsername(username);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByPhone(String phone) {
		return userRepository.existsByPhone(phone);
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
		return userRepository.countByStatus(status);
	}

	@Override
	public long countByRole(Role role) {
		return userRepository.countByRole(role);
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
		return userRepository.findByStatus(status, pageable);
	}

	@Override
	public Page<User> findByUsernameContainingIgnoreCase(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

}
