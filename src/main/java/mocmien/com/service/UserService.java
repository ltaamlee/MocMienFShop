package mocmien.com.service;

import java.util.List;
import java.util.Optional;

import mocmien.com.entity.User;

public interface UserService {

	List<User> getAllUsers();

    User getUserById(Integer id);
    
    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    User createUser(User user);

    User updateUser(Integer id, User user);

    void deleteUser(Integer id);

    boolean existsById(Integer id);

    boolean existsByEmail(String email);
}
