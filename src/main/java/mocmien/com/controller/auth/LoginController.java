package mocmien.com.controller.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import mocmien.com.dto.request.auth.LoginRequest;
import mocmien.com.entity.User;
import mocmien.com.enums.UserStatus;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.security.jwt.JwtTokenProvider;
import mocmien.com.service.UserService;

@Controller
public class LoginController {

	private final UserService userService;
	private final JwtTokenProvider tokenProvider;

	public LoginController(UserService userService, JwtTokenProvider tokenProvider) {
		this.userService = userService;
		this.tokenProvider = tokenProvider;
	}

	// ===================== LOGIN FORM =====================
	@GetMapping("/login")
	public String showLogin() {
		return "auth/login";
	}

	@PostMapping("/login")
	public String login(@Valid @ModelAttribute LoginRequest loginRequest, Model model, HttpServletResponse response) {
		try {
			// Gọi service để xác thực user
			Optional<User> userOpt = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
			User user = userOpt.orElseThrow(() -> new RuntimeException("Đăng nhập thất bại!"));

			// Cập nhật trạng thái ONLINE + thời gian đăng nhập
			user.setStatus(UserStatus.ONLINE);
			user.setLastLoginAt(LocalDateTime.now());
			userService.save(user);

			// Tạo JWT token cookie
			String token = tokenProvider.generateToken(user.getUsername());
			Cookie cookie = new Cookie("JWT_TOKEN", token);
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			cookie.setMaxAge(24 * 60 * 60); // 1 ngày
			response.addCookie(cookie);

			// Điều hướng theo role
			return switch (user.getRole().getRoleName()) {
			case ADMIN -> "redirect:/admin/dashboard";
			case VENDOR -> "redirect:/vendor/dashboard";
			case SHIPPER -> "redirect:/shipper/dashboard";
			case CUSTOMER -> "redirect:/home";
			default -> "redirect:/login";
			};

		} catch (RuntimeException ex) {
			// Nếu có lỗi (VD: tài khoản không tồn tại, sai mật khẩu,...)
			model.addAttribute("error", ex.getMessage());
			return "auth/login";
		}
	}

	// ===================== REFRESH TOKEN =====================
	@GetMapping("/refresh")
	public String refreshToken(HttpServletResponse response,
			@CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken) {

		if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
			return "redirect:/login";
		}

		String username = tokenProvider.getUsernameFromToken(refreshToken);
		String newAccessToken = tokenProvider.generateToken(username);

		Cookie accessCookie = new Cookie("JWT_TOKEN", newAccessToken);
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		accessCookie.setMaxAge(24 * 60 * 60);
		response.addCookie(accessCookie);

		return "redirect:/home";
	}

	// ===================== LOGOUT =====================
	@GetMapping("/logout")
	public String logout(@CookieValue(value = "JWT_TOKEN", required = false) String jwtToken,
			HttpServletResponse response) {

		// Nếu có token thì tìm user từ đó
		if (jwtToken != null && tokenProvider.validateToken(jwtToken)) {
			String username = tokenProvider.getUsernameFromToken(jwtToken);
			Optional<User> userOpt = userService.findByUsername(username);

			if (userOpt.isPresent()) {
				User user = userOpt.get();
				user.setStatus(UserStatus.OFFLINE);
				userService.save(user);
			}
		}

		// Xóa cookie
		Cookie accessCookie = new Cookie("JWT_TOKEN", null);
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		accessCookie.setMaxAge(0);
		response.addCookie(accessCookie);

		Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(0);
		response.addCookie(refreshCookie);

		// Xóa context
		SecurityContextHolder.clearContext();

		return "redirect:/home";
	}

	// ===================== LOGOUT WITH GOOGLE=====================
	@GetMapping("/logout/google")
	public String logoutGoogle(@CookieValue(value = "JWT_TOKEN", required = false) String jwtToken,
			HttpServletResponse response) {

		if (jwtToken != null && tokenProvider.validateToken(jwtToken)) {
			String username = tokenProvider.getUsernameFromToken(jwtToken);
			Optional<User> userOpt = userService.findByUsername(username);
			userOpt.ifPresent(user -> {
				user.setStatus(UserStatus.OFFLINE);
				userService.save(user);
			});
		}

		// Xóa cookie JWT
		Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(0);
		response.addCookie(jwtCookie);

		// Xóa refresh token nếu có
		Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(0);
		response.addCookie(refreshCookie);

		// Xóa context
		SecurityContextHolder.clearContext();

		return "redirect:https://accounts.google.com/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:8080/home";
	}
}
