package mocmien.com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import mocmien.com.entity.User;
import mocmien.com.service.UserService;

@ControllerAdvice
@Component
public class GlobalModelAttributes {

	@Autowired
	private UserService userService;

	@ModelAttribute
	public void addLoggedInUser(Model model, Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			User user = userService.findByUsername(username).orElse(null);
			model.addAttribute("user", user);
			model.addAttribute("avatarUrl", user != null ? user.getAvatar() : null);
		} else {
			model.addAttribute("user", null);
			model.addAttribute("avatarUrl", null);
		}
	}
}
