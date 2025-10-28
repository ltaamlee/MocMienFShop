package mocmien.com.controller.customer;

import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;
import mocmien.com.service.UserService;
import mocmien.com.service.CloudinaryService;
import mocmien.com.service.UserProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private CloudinaryService cloudinaryService;
    
    @GetMapping
    public String showAccountPage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        model.addAttribute("user", user);
        return "customer/account";
    }


    @PostMapping("/avatar/upload")
    @ResponseBody
    public Map<String, String> uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal) {
        if (principal == null || file.isEmpty()) {
            return Map.of("error", "Không tìm thấy tài khoản hoặc file trống");
        }

        try {
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Upload lên Cloudinary
            String folder = "avatars/" + username;
            String imageUrl = cloudinaryService.upload(file, folder);

            // Cập nhật user avatar
            user.setAvatar(imageUrl);
            userService.save(user);

            return Map.of("url", imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Lỗi upload: " + e.getMessage());
        }
    }
    
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateUserProfile(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
            @RequestParam(required = false) String phone,
            @ModelAttribute UserProfile formProfile,
            Principal principal) {

        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
            }

            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            UserProfile profile = user.getUserProfile();
            if (profile == null) {
                profile = new UserProfile();
                profile.setUser(user);
            }

            // ✅ Cập nhật thông tin profile
            profile.setFullName(formProfile.getFullName());
            profile.setGender(formProfile.getGender());
            if (dob != null) profile.setDob(dob);

            // ✅ Cập nhật phone cho User
            if (phone != null && !phone.isBlank()) {
                user.setPhone(phone);
            }

            // 🔹 Lưu
            userProfileService.save(profile);
            userService.save(user);

            // ✅ Response trả về JSON
            Map<String, Object> response = new HashMap<>();
            response.put("fullName", profile.getFullName());
            response.put("dob", profile.getDob());
            response.put("gender", profile.getGender());
            response.put("phone", user.getPhone());
            response.put("email", user.getEmail());
            response.put("point", profile.getPoint());
            response.put("eWallet", profile.geteWallet());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Đã xảy ra lỗi khi lưu thông tin: " + e.getMessage());
        }
    }



}
