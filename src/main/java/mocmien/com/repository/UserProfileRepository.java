package mocmien.com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer>{

    Optional<UserProfile> findByUser(User user);

}
