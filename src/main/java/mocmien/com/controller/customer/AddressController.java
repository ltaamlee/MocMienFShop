package mocmien.com.controller.customer;

import mocmien.com.entity.CustomerAddress;
import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;
import mocmien.com.service.CustomerAddressService;
import mocmien.com.service.UserProfileService;
import mocmien.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/account/address")
public class AddressController {

    @Autowired
    private CustomerAddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    /* ===============================================
       🏠 1️⃣ Hiển thị danh sách địa chỉ của người dùng
    =============================================== */
    @GetMapping
    public String listAddresses(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Lấy hoặc tạo hồ sơ người dùng
        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = userProfileService.findByUser(user).orElse(null);
            if (profile == null) {
                profile = new UserProfile();
                profile.setUser(user);
                user.setUserProfile(profile);
                userProfileService.save(profile);
            }
        }

        List<CustomerAddress> addresses = addressService.findByCustomer(profile);

        model.addAttribute("user", user);
        model.addAttribute("userProfile", profile);
        model.addAttribute("addresses", addresses);
        
        System.out.println("🔹 User: " + username);
        System.out.println("🔹 Profile ID: " + profile.getId());
        System.out.println("🔹 Số địa chỉ tìm thấy: " + addresses.size());

        return "customer/account-address";
    }


    /* ===============================================
       ✏️ 2️⃣ Thêm hoặc cập nhật địa chỉ
    =============================================== */
    @PostMapping("/save")
    public String saveAddress(@ModelAttribute CustomerAddress address, Principal principal) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
            user.setUserProfile(profile);
            userProfileService.save(profile);
        }

        address.setCustomer(profile);

        // Nếu là địa chỉ đầu tiên → đặt mặc định
        if (addressService.findByCustomer(profile).isEmpty()) {
            address.setIsDefault(true);
        }

        addressService.save(address);
        return "redirect:/account/address";
    }


    /* ===============================================
       🗑️ 3️⃣ Xóa địa chỉ
    =============================================== */
    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer id) {
        addressService.delete(id);
        return "redirect:/account/address";
    }

    /* ===============================================
       🌟 4️⃣ Đặt làm mặc định
    =============================================== */
    @GetMapping("/set-default/{id}")
    public String setDefault(@PathVariable("id") Integer id, Principal principal) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        UserProfile profile = user.getUserProfile();

        if (profile != null) {
            addressService.setDefault(id, profile);
        }
        return "redirect:/account/address";
    }
    
    @PostMapping("/add-ajax")
    @ResponseBody
    public CustomerAddress addAddressAjax(@ModelAttribute CustomerAddress address, Principal principal) {
        if (principal == null) throw new RuntimeException("Chưa đăng nhập!");

        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
            user.setUserProfile(profile);
            userProfileService.save(profile);
        }

        address.setCustomer(profile);

        // Nếu là địa chỉ đầu tiên thì đặt mặc định
        if (addressService.findByCustomer(profile).isEmpty()) {
            address.setIsDefault(true);
        }

        return addressService.save(address);
    }

}
