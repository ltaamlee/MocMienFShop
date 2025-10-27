package mocmien.com.controller.vendor;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('VENDOR')")
public class VendorController {

	@Autowired
	private UserService userService;

	private void addUserToModel(CustomUserDetails userDetails, Model model) {
		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}
	}
	// Quản lý Trang chủ
	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		addUserToModel(userDetails, model);
		return "vendor/dashboard";
	}

	// Đăng ký Shop
	@GetMapping("/register-shop")
	public String registerShop(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		addUserToModel(userDetails, model);
		return "vendor/register-shop";
	}

	// Quản lý Sản phẩm
	@GetMapping("/products")
	public String products(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		addUserToModel(userDetails, model);
		return "vendor/products";
	}

	// Quản lý Đơn hàng
	@GetMapping("/orders")
	public String orders(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		addUserToModel(userDetails, model);
		return "vendor/orders";
	}

	// Quản lý Khuyến mãi
	@GetMapping("/promotions")
	public String promotions(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		addUserToModel(userDetails, model);
		return "vendor/promotions";
	}

	// Quản lý Doanh thu
	@GetMapping("/revenue")
	public String revenue(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		addUserToModel(userDetails, model);
		return "vendor/revenue";
	}

	// Cài đặt
	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "vendor/profile";
	}
}
