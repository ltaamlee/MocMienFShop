package mocmien.com.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import mocmien.com.entity.User;
import mocmien.com.enums.RoleName;
import mocmien.com.security.jwt.JwtTokenProvider;
import mocmien.com.service.RoleService;
import mocmien.com.service.UserService;

@Controller
public class RegisterController {

	@Autowired
    private UserService userService;
	@Autowired
    private RoleService roleService;
	@Autowired
    private PasswordEncoder passwordEncoder; 
	@Autowired
    private JwtTokenProvider tokenProvider;

    
    @GetMapping("/register")
    public String showRegister(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           @RequestParam("fullName") String fullName,
                           Model model) {
        try {
            userService.register(user, RoleName.CUSTOMER, fullName);
            model.addAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
            // Reset lại form
            model.addAttribute("user", new User());
            return "auth/register"; // vẫn ở trang đăng ký
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
    }



}