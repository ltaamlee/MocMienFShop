package mocmien.com.service;

import java.util.Optional;

import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;

public interface UserProfileService {
	
	Optional<UserProfile> findByUser(User user);

	UserProfile save(UserProfile profile);
}
