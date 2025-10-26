package mocmien.com.controller.vendor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mocmien.com.entity.User;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.UserService;

@Controller
@RequestMapping("/vendor")
public class VendorController {
	
	@Autowired
	private UserService userService;

	//Quản lý shop 
	/*
	 * Đăng ký shop
	 * Thay logo, đặt tên shop, cấu hình các phương thức cơ bản
	 * */
	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails != null) {
            Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
            userOpt.ifPresent(user -> model.addAttribute("user", user));
        }

        return "vendor/dashboard";
    }

	
	//Quản lý sản phẩm
	@GetMapping("/product")
	public String product(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails != null) {
            Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
            userOpt.ifPresent(user -> model.addAttribute("user", user));
        }

        return "vendor/product";
    }
	
	//Quản lý đơn hàng
	@GetMapping("/order")
	public String order(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails != null) {
            Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
            userOpt.ifPresent(user -> model.addAttribute("user", user));
        }

        return "vendor/order";
    }
	
	//Quản lý khuyến mãi
	@GetMapping("/promotion")
	public String promotion(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails != null) {
            Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
            userOpt.ifPresent(user -> model.addAttribute("user", user));
        }

        return "vendor/promotion";
    }
	
	
}
