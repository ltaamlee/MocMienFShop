package mocmien.com.controller.admin;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import mocmien.com.entity.User;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.UserService;

@RestController
@RequestMapping("/admin/profile")
public class AdminProfileController {

    @Autowired
    private UserService userService;

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String avatarUrl,
            @RequestParam(required = false) String password
    ) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Người dùng chưa đăng nhập!"
            ));
        }

        // Lấy username từ CustomUserDetails
        String username = userDetails.getUsername();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Không tìm thấy người dùng!"
            ));
        }

        User currentUser = userOpt.get();

        // Cập nhật User
        if (email != null && !email.isEmpty()) currentUser.setEmail(email);
        if (phone != null && !phone.isEmpty()) currentUser.setPhone(phone);
        if (avatarUrl != null && !avatarUrl.isEmpty()) currentUser.setAvatar(avatarUrl);
        if (password != null && !password.isEmpty()) {
            // encode nếu dùng Spring Security
            currentUser.setPassword(password);
        }

        // Cập nhật UserProfile
        if (currentUser.getUserProfile() != null) {
            if (name != null && !name.isEmpty()) currentUser.getUserProfile().setFullName(name);
            if (dob != null) currentUser.getUserProfile().setDob(dob);
            if (gender != null && !gender.isEmpty()) currentUser.getUserProfile().setGender(gender);
        }

        userService.save(currentUser);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Cập nhật profile thành công!"
        ));
    }
}
