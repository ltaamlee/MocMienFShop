package mocmien.com.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import mocmien.com.entity.User;
import mocmien.com.enums.RoleName;
import mocmien.com.service.UserService;

@Controller
public class RegisterController {

	@Autowired
    private UserService userService;

    
    @GetMapping("/register")
    public String showRegister(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user,
                           @RequestParam(value = "fullName", required = false) String fullName,
                           Model model) {
        try {
            // Debug log TRƯỚC KHI validate
            System.out.println("========== DEBUG REGISTER ==========");
            System.out.println("User object: " + user);
            if (user != null) {
                System.out.println("Username: " + user.getUsername());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Phone: " + user.getPhone());
            }
            System.out.println("Full Name: " + fullName);
            System.out.println("====================================");
            
            userService.register(user, RoleName.CUSTOMER, fullName);
            model.addAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
            // Reset lại form
            model.addAttribute("user", new User());
            return "auth/register";
        } catch (RuntimeException ex) {
            ex.printStackTrace(); // In stack trace để debug
            model.addAttribute("error", ex.getMessage());
            // ✅ QUAN TRỌNG: Phải set lại user object vào model khi có lỗi
            if (user == null) {
                model.addAttribute("user", new User());
            } else {
                model.addAttribute("user", user);
            }
            return "auth/register";
        }
    }



}