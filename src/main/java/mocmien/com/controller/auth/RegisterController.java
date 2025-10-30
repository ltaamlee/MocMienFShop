package mocmien.com.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import mocmien.com.dto.request.auth.RegisterRequest;
import mocmien.com.entity.User;
import mocmien.com.enums.RoleName;
import mocmien.com.service.UserService;

@Controller
public class RegisterController {

	@Autowired
    private UserService userService;

    
    @GetMapping("/register")
    public String showRegister(Model model) {
        RegisterRequest registerRequest = new RegisterRequest();
        model.addAttribute("registerRequest", registerRequest);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult bindingResult,
                           Model model) {
        try {
            // Debug log
            System.out.println("========== DEBUG REGISTER ==========");
            System.out.println("RegisterRequest: " + request);
            if (request != null) {
                System.out.println("Username: " + request.getUsername());
                System.out.println("Email: " + request.getEmail());
                System.out.println("Phone: " + request.getPhone());
                System.out.println("FullName: " + request.getFullName());
                System.out.println("Role: " + request.getRole()); // ✅ LOG ROLE
            }
            System.out.println("====================================");
            
            // Kiểm tra validation errors
            if (bindingResult.hasErrors()) {
                System.out.println("Validation errors: " + bindingResult.getAllErrors());
                // Giữ lại data đã nhập
                return "auth/register";
            }
            
            // Convert RegisterRequest sang User entity
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setPhone(request.getPhone());
            
            // ✅ Parse role từ request
            RoleName roleName;
            try {
                roleName = RoleName.valueOf(request.getRole());
            } catch (Exception e) {
                roleName = RoleName.CUSTOMER; // Mặc định nếu parse lỗi
            }
            
            userService.register(user, roleName, request.getFullName());
            
            model.addAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
            // Reset form
            model.addAttribute("registerRequest", new RegisterRequest());
            return "auth/register";
            
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
    }



}