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
    public String login(@Valid @ModelAttribute LoginRequest loginRequest,
                        Model model,
                        HttpServletResponse response) {

        Optional<User> userOpt = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng / Tài khoản không thể đăng nhập / Tài khoản ngừng hoạt động!");
            return "auth/login";
        }

        User user = userOpt.get();

        // Set status ONLINE + cập nhật last login
        user.setStatus(UserStatus.ONLINE);
        user.setLastLoginAt(LocalDateTime.now());
        userService.save(user);

        // Tạo JWT cookie
        String token = tokenProvider.generateToken(user.getUsername());
        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        // Redirect theo role
        return switch (user.getRole().getRoleName()) {
            case ADMIN -> "redirect:/admin/dashboard";
            case VENDOR -> "redirect:/vendor/dashboard";
            case SHIPPER -> "redirect:/shipper/dashboard";
            case CUSTOMER -> "redirect:/home";
            default -> "redirect:/login";
        };
    }

    // ===================== GOOGLE / OAUTH LOGIN =====================
    @GetMapping("/oauth2/loginSuccess")
    public String oauth2LoginSuccess(@AuthenticationPrincipal CustomUserDetails userDetails,
                                     HttpServletResponse response) {

        User user = userDetails.getUser();

        // Set ONLINE + lastLoginAt
        user.setStatus(UserStatus.ONLINE);
        user.setLastLoginAt(LocalDateTime.now());
        userService.save(user);

        // JWT cookie
        String token = tokenProvider.generateToken(user.getUsername());
        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        // Redirect theo role
        return switch (user.getRole().getRoleName()) {
            case ADMIN -> "redirect:/admin/dashboard";
            case VENDOR -> "redirect:/vendor/dashboard";
            case SHIPPER -> "redirect:/shipper/dashboard";
            case CUSTOMER -> "redirect:/home";
            default -> "redirect:/login";
        };
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
    public String logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                         HttpServletResponse response) {

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

        // Set user OFFLINE
        if (userDetails != null) {
            User user = userDetails.getUser();
            user.setStatus(UserStatus.OFFLINE);
            userService.save(user);
        }

        // Xóa context
        SecurityContextHolder.clearContext();

        return "redirect:/home";
    }
}
