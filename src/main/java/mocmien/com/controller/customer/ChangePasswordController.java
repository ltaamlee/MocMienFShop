package mocmien.com.controller.customer;

import mocmien.com.entity.User;
import mocmien.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/account")
public class ChangePasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* ===== Trang đổi mật khẩu ===== */
    @GetMapping("/change-password")
    public String showChangePasswordPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        model.addAttribute("user", user);
        return "customer/change-password";
    }

    /* ===== Xử lý đổi mật khẩu ===== */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal,
                                 Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy người dùng!");
            return "customer/change-password";
        }

        User user = userOpt.get();

        // 1️⃣ Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "customer/change-password";
        }

        // 2️⃣ Mật khẩu mới không được trùng mật khẩu cũ
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            model.addAttribute("error", "Mật khẩu mới không được trùng với mật khẩu cũ!");
            return "customer/change-password";
        }

        // 3️⃣ Mật khẩu xác nhận phải trùng
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp với mật khẩu mới!");
            return "customer/change-password";
        }

        // 4️⃣ Kiểm tra độ mạnh của mật khẩu mới
        if (!isStrongPassword(newPassword)) {
            model.addAttribute("error",
                    "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
            return "customer/change-password";
        }

        // ✅ 5️⃣ Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        model.addAttribute("success", "Đổi mật khẩu thành công!");
        return "customer/change-password";
    }

    /* ==== Hàm kiểm tra mật khẩu mạnh ==== */
    private boolean isStrongPassword(String password) {
        // Ít nhất 8 ký tự, có: 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.matches(regex, password);
    }
}
