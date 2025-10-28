package mocmien.com.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;
import mocmien.com.repository.UserProfileRepository;
import mocmien.com.service.UserProfileService;

@Service
public class UserProfileServiceImpl implements UserProfileService {

	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Override
    public Optional<UserProfile> findByUser(User user) {
        return userProfileRepository.findByUser(user);
    }
	
	@Override
	public UserProfile save(UserProfile profile) {
        return userProfileRepository.save(profile);
    }
}
