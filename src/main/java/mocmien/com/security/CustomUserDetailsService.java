package mocmien.com.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import mocmien.com.entity.User;
import mocmien.com.service.UserService;


@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
    private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
	    Optional<User> userOpt = userService.findByEmail(usernameOrEmail); // ưu tiên email

	    if (userOpt.isEmpty()) {
	        userOpt = userService.findByUsername(usernameOrEmail);
	    }

	    User user = userOpt.orElseThrow(() ->
	            new UsernameNotFoundException("Không tìm thấy người dùng: " + usernameOrEmail)
	    );

	    return new CustomUserDetails(user);
	}

}