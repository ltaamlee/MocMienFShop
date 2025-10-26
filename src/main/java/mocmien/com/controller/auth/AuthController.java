package mocmien.com.controller.auth;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import mocmien.com.dto.request.auth.LoginRequest;
import mocmien.com.entity.User;
import mocmien.com.security.jwt.JwtTokenProvider;
import mocmien.com.service.UserService;

public class AuthController {
	private final UserService userService;
	private final JwtTokenProvider tokenProvider;

	public AuthController(UserService userService, JwtTokenProvider tokenProvider) {
		this.userService = userService;
		this.tokenProvider = tokenProvider;
	}

	@GetMapping("/login")
	public String showLogin() {
		return "auth/login";
	}

	@PostMapping("/login")
	public String login(@Valid @ModelAttribute LoginRequest loginRequest, Model model, HttpServletResponse response) {

		Optional<User> userOpt = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

		if (userOpt.isEmpty()) {
			model.addAttribute("error",
					"Tên đăng nhập hoặc mật khẩu không đúng / Tài khoản không thể đăng nhập / Tài khoản ngừng hoạt động!");
			return "auth/login";
		}

		User user = userOpt.get();
		if (!user.isActive()) {
			model.addAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
			return "auth/login";
		}

		String token = tokenProvider.generateToken(user.getUsername());

		Cookie cookie = new Cookie("JWT_TOKEN", token);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(24 * 60 * 60);
		response.addCookie(cookie);

		return switch (user.getRole().getRoleName()) {
		case ADMIN -> "redirect:/admin/dashboard";
		case VENDOR -> "redirect:/seller/dashboard";
		case SHIPPER -> "redirect:/shipper/dashboard";
		case CUSTOMER -> "redirect:/home";
		default -> "redirect:/auth/login";
		};
	}

	@GetMapping("/logout")
	public String logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("JWT_TOKEN", null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		SecurityContextHolder.clearContext();

		return "redirect:/home";
	}
}
