package mocmien.com.controller.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mocmien.com.entity.User;
import mocmien.com.security.jwt.JwtTokenProvider;
import mocmien.com.service.UserService;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserService userService;
	private final JwtTokenProvider tokenProvider;

	public OAuth2LoginSuccessHandler(UserService userService, JwtTokenProvider tokenProvider) {
		this.userService = userService;
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		System.out.println("===== OAuth2 Login Success Handler =====");
		System.out.println("Authentication principal class: " + authentication.getPrincipal().getClass().getName());

		if (!(authentication.getPrincipal() instanceof OAuth2User oauthUser)) {
			response.sendRedirect("/login?error=true");
			return;
		}

		String email = oauthUser.getAttribute("email");
		String fullName = oauthUser.getAttribute("name");

		// Tạo hoặc lấy user đã có
		User user = userService.createOAuthUser(email, fullName);

		// Tạo JWT cookie
		String token = tokenProvider.generateToken(user.getUsername());
		Cookie cookie = new Cookie("JWT_TOKEN", token);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(24 * 60 * 60);
		response.addCookie(cookie);

		// Redirect theo role
		String redirectUrl = switch (user.getRole().getRoleName()) {
		case ADMIN -> "/admin/dashboard";
		case VENDOR -> "/vendor/dashboard";
		case SHIPPER -> "/shipper/dashboard";
		case CUSTOMER -> "/home";
		default -> "/login";
		};

		response.sendRedirect(redirectUrl);
	}

}
