package mocmien.com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer>{

    Optional<UserProfile> findByUser(User user);

}
