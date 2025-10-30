package mocmien.com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${google.maps.apiKey:}")
	private String googleMapsApiKey;

	@ModelAttribute
	public void addLoggedInUser(Model model, Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			User loggedInUser = userService.findByUsername(username).orElse(null);
			model.addAttribute("loggedInUser", loggedInUser);  // ✅ Đổi tên để không conflict
			model.addAttribute("avatarUrl", loggedInUser != null ? loggedInUser.getAvatar() : null);
		} else {
			model.addAttribute("loggedInUser", null);
			model.addAttribute("avatarUrl", null);
		}

		model.addAttribute("googleMapsApiKey", googleMapsApiKey);
	}
}
