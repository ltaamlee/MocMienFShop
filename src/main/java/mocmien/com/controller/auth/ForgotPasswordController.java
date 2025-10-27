package mocmien.com.controller.auth;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import mocmien.com.entity.User;
import mocmien.com.repository.UserRepository;
import mocmien.com.service.EmailService;
import mocmien.com.service.UserService;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;

    // Lưu OTP tạm thời trong session hoặc map (demo)
    private Map<String, String> otpStorage = new HashMap<>();
    private Map<String, LocalDateTime> otpExpiry = new HashMap<>();

    // ==================== HIỂN THỊ FORM QUÊN MẬT KHẨU ====================
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    // ==================== GỬI OTP TỚI EMAIL (DEMO) ====================
    @PostMapping("/forgot-password")
    public String sendOtp(@RequestParam("email") String email, Model model, HttpSession session) {

        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
            return "auth/forgot-password";
        }

        // Sinh OTP 6 số
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);
        otpExpiry.put(email, LocalDateTime.now().plusMinutes(5));
        user.setCode(otp);
        userService.save(user);

        //Gửi OTP qua email thật (ở đây chỉ log demo)
        System.out.println("OTP gửi cho " + email + ": " + otp);
        emailService.sendOtpEmail(email, otp);


        // Lưu email vào session để dùng ở bước nhập OTP
        session.setAttribute("resetEmail", email);

        model.addAttribute("success", "Mã OTP đã được gửi tới email của bạn!");
        return "redirect:/verify-otp";
    }

    // ==================== FORM NHẬP OTP ====================
    @GetMapping("/verify-otp")
    public String showOtpForm() {
        return "auth/verify-otp";
    }

    // ==================== XÁC NHẬN OTP ====================
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            model.addAttribute("error", "Phiên làm việc hết hạn. Vui lòng thử lại.");
            return "redirect:/forgot-password";
        }

        String correctOtp = otpStorage.get(email);
        LocalDateTime expiry = otpExpiry.get(email);

        if (correctOtp == null || expiry == null || expiry.isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Mã OTP đã hết hạn. Vui lòng thử lại.");
            return "auth/verify-otp";
        }

        if (!otp.equals(correctOtp)) {
            model.addAttribute("error", "Mã OTP không chính xác!");
            return "auth/verify-otp";
        }

        // OTP đúng -> chuyển sang trang đặt lại mật khẩu
        session.setAttribute("verifiedEmail", email);
        return "redirect:/reset-password";
    }

    // ==================== FORM ĐẶT LẠI MẬT KHẨU ====================
    @GetMapping("/reset-password")
    public String showResetForm() {
        return "auth/reset-password";
    }

    // ==================== CẬP NHẬT MẬT KHẨU ====================
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("verifiedEmail");
        if (email == null) {
            model.addAttribute("error", "Phiên đặt lại mật khẩu không hợp lệ!");
            return "redirect:/forgot-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "auth/reset-password";
        }

        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Không tìm thấy người dùng!");
            return "auth/reset-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        // Xóa session & OTP
        session.removeAttribute("resetEmail");
        session.removeAttribute("verifiedEmail");
        otpStorage.remove(email);
        otpExpiry.remove(email);

        model.addAttribute("success", "Mật khẩu đã được cập nhật thành công!");
        return "redirect:/login";
    }
}
