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

import mocmien.com.entity.Customer;
import mocmien.com.entity.Level;
import mocmien.com.entity.Role;
import mocmien.com.entity.User;
import mocmien.com.enums.Rank;
import mocmien.com.enums.RoleName;
import mocmien.com.enums.UserStatus;
import mocmien.com.repository.CustomerRepository;
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
	private CustomerRepository customerRepository;
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

	    Optional<User> existingUserOpt = userRepository.findByEmail(email);
	    if (existingUserOpt.isPresent()) {
	        User existingUser = existingUserOpt.get();
	        existingUser.setStatus(UserStatus.ONLINE);
	        existingUser.setLastLoginAt(LocalDateTime.now());
	        return userRepository.save(existingUser);
	    }

	    // Tạo User mới
	    User user = new User();
	    user.setEmail(email);
	    user.setUsername(email.split("@")[0]);

	    // Nếu phone là NOT NULL, set giá trị tạm
	    user.setPhone("0000000000"); // giá trị placeholder
	    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
	    user.setActive(true);
	    user.setStatus(UserStatus.ONLINE);
	    user.setLastLoginAt(LocalDateTime.now());

	    Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER)
	            .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại"));
	    user.setRole(customerRole);

	    User savedUser = userRepository.save(user);

	    // Tạo Customer
	    Customer customer = new Customer();
	    customer.setUser(savedUser);
	    customer.setFullName(fullName);
	    
	    Level basicLevel = levelRepository.findByName(Rank.NEW)
	            .orElseThrow(() -> new RuntimeException("Level NEW không tồn tại"));
	    
	    // Nếu level là NOT NULL, set giá trị mặc định
	    customer.setLevel(basicLevel);
	    customerRepository.save(customer);

	    return savedUser;
	}


	@Override
	public Optional<User> login(String Username, String password) {
		Optional<User> userOpt = userRepository.findByUsername(Username);

		User user = userOpt.get();

		boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
		UserStatus status = user.getStatus();

		System.out.printf("[LOGIN] User: %s | Role: %s | Status: %s | Password match: %s%n", user.getUsername(),
				user.getRole().getRoleName(), status, passwordMatch);

		// Kiểm tra trạng thái
		if (!user.isActive()) {
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
		Customer customer = new Customer();
		customer.setUser(savedUser);
		customer.setFullName(fullName);
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
		// TODO Auto-generated method stub

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
