package mocmien.com.controller.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mocmien.com.entity.User;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	@Autowired
	private UserService userService;
	
	//Trang chủ
	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "admin/dashboard";
	}
	
	//Quản lý shop
	
	//Quản lý 
}
