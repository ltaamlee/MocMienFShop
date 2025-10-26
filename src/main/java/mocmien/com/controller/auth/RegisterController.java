package mocmien.com.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
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
    public String processRegister(@ModelAttribute("user") User user,
                                  @RequestParam("fullName") String fullName,
                                  Model model,
                                  HttpServletResponse response) {

    	try {
            // Gọi service để đăng ký user với role CUSTOMER
            userService.register(user, RoleName.CUSTOMER, fullName);

            // Sau khi đăng ký thành công, hiển thị thông báo
            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "auth/login"; // Chuyển hướng tới trang login

        } catch (RuntimeException e) {
            // Nếu có lỗi (email hoặc username trùng, role không tồn tại, v.v.)
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        } 
    }
}