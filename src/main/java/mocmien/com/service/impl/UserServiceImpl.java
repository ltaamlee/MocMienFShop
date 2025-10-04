package mocmien.com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import mocmien.com.entity.User;
import mocmien.com.repository.UserRepository;
import mocmien.com.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUserById(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}
	
	@Override
	public Optional<User> getUserByUsername(String username) {
		return userRepository.findByEmail(username);
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User createUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("User already exists with email: " + user.getEmail());
		}
		return userRepository.save(user);
	}

	@Override
	public User updateUser(Integer id, User user) {
		User existing = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

		existing.setFullName(user.getFullName());
		existing.setEmail(user.getEmail());
		existing.setPhone(user.getPhone());
		existing.setAddress(user.getAddress());
		existing.setPasswordHash(user.getPasswordHash());
		existing.setStatus(user.getStatus());
		existing.setRole(user.getRole());
		existing.setUpdatedAt(user.getUpdatedAt());

		return userRepository.save(existing);
	}

	@Override
	public void deleteUser(Integer id) {
		if (!userRepository.existsById(id)) {
			throw new RuntimeException("User not found with id: " + id);
		}
		userRepository.deleteById(id);
	}

	@Override
	public boolean existsById(Integer id) {
		return userRepository.existsById(id);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
}
