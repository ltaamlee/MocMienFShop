package mocmien.com.controller.shipper;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mocmien.com.entity.Delivery;
import mocmien.com.entity.User;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.DeliveryService;
import mocmien.com.service.UserService;

@Controller
@RequestMapping("/shipper")
public class ShipperController {
	@Autowired
	private UserService userService;

	@Autowired
	private DeliveryService deliveryService;

	// Trang chủ
	@GetMapping("/login")
	public String showLogin() {
		return "shipper/login";
	}

	@GetMapping("/register")
	public String showRegister(Model model) {
		User user = new User();
		model.addAttribute("user", user);

		List<Delivery> deliveryCompanies = deliveryService.getAllActiveDeliveries();
		model.addAttribute("deliveryCompanies", deliveryCompanies);

		return "shipper/register";
	}

	// Trang chủ
	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "shipper/dashboard";
	}

	// Đơn hàng
	@GetMapping("/order")
	public String order(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "shipper/order";
	}

	// Đơn hàng
	@GetMapping("/ewallet")
	public String ewallet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "shipper/ewallet";
	}

	// Đơn hàng
	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "shipper/profile";
	}
}
