package mocmien.com.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mocmien.com.dto.response.category.CategoryResponse;
import mocmien.com.entity.Category;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;
import mocmien.com.enums.RoleName;
import mocmien.com.enums.UserStatus;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.CategoryService;
import mocmien.com.service.StoreService;
import mocmien.com.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private UserService userService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private StoreService storeService;

	// Trang chủ
	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "admin/dashboard";
	}

	// Trang chủ
	@GetMapping("/user")
	public String user(@AuthenticationPrincipal CustomUserDetails userDetails, Model model,
			@RequestParam(defaultValue = "10") int size) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}
		model.addAttribute("statuses", UserStatus.values());
		model.addAttribute("roles", RoleName.values());

		return "admin/user";
	}

	// Quản lý shop
	@GetMapping("/store")
	public String store(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "admin/store";
	}

	// Quản lý danh mục
	@GetMapping("/category")
	public String category(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		List<CategoryResponse> categories = categoryService.getAllCategories();
		model.addAttribute("categories", categories);

		return "admin/category";
	}

	// Quản lý sản phẩm
	@GetMapping("/product")
	public String product(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}
		List<Category> categories = categoryService.getActiveCategories();
		model.addAttribute("categoriesActive", categories);
		List<Store> allStores = storeService.getAll(); 
        model.addAttribute("allStores", allStores);
		return "admin/product";
	}

	// Quản lý khuyến mãi và chiết khẩu app
	@GetMapping("/promotion")
	public String promotion(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		model.addAttribute("promotionTypes", PromotionType.values());
		model.addAttribute("promotionStatuses", PromotionStatus.values());
		List<Store> allStores = storeService.getAll();
		model.addAttribute("allStores", allStores);
		return "admin/promotion";
	}

	// Theo dõi khuyến mãi cửa hàng (admin)
	@GetMapping("/promotion/stores")
	public String promotionStores(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		model.addAttribute("promotionTypes", PromotionType.values());
		model.addAttribute("promotionStatuses", PromotionStatus.values());
		return "admin/promotion_stores";
	}

	// Quản lý đơn vị vận chuyển
	@GetMapping("/delivery")
	public String delivery(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "admin/delivery";
	}

	// Quản lý đơn vị vận chuyển
	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

		if (userDetails != null) {
			Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
			userOpt.ifPresent(user -> model.addAttribute("user", user));
		}

		return "admin/profile";
	}
}
