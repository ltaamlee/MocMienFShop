package mocmien.com.controller.customer;

import mocmien.com.entity.User;
import mocmien.com.service.UserService;
import mocmien.com.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

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
}
