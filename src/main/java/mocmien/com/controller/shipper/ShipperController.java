package mocmien.com.controller.shipper;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import mocmien.com.entity.User;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.AdminDeliveryService;
import mocmien.com.service.UserService;

@Controller
@RequestMapping("/shipper")
public class ShipperController {
	@Autowired
	private UserService userService;

	@Autowired
	private AdminDeliveryService deliveryService;

	// Trang chủ
	@GetMapping("/login")
	public String showLogin() {
		return "shipper/login";
	}


	@GetMapping("/dashboard")
	@PreAuthorize("hasRole('SHIPPER')")
	public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, 
	                        HttpServletRequest request, 
	                        Model model) {

	    if (userDetails != null) {
	        userService.findByUsername(userDetails.getUsername())
	                   .ifPresent(user -> model.addAttribute("user", user));
	    }

	    // ✅ Thêm dòng này
	    model.addAttribute("currentPath", request.getRequestURI());

	    return "shipper/dashboard";
	}


	// Đơn hàng
    @GetMapping("/order")
    @PreAuthorize("hasRole('SHIPPER')")
	public String order(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

        return "shipper/order";
	}

	// Đơn hàng
    @GetMapping("/profile")
    @PreAuthorize("hasRole('SHIPPER')")
	public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

        return "shipper/profile";
	}
}
