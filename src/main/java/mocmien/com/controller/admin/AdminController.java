package mocmien.com.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("pageTitle", "Dashboard");
		return "admin/dashboard";
	}

	@GetMapping("/users")
	public String users(Model model) {
		model.addAttribute("pageTitle", "Quản lý User");
		return "admin/users";
	}

	@GetMapping("/products")
	public String products(Model model) {
		model.addAttribute("pageTitle", "Quản lý Sản phẩm");
		return "admin/products";
	}

	@GetMapping("/categories")
	public String categories(Model model) {
		model.addAttribute("pageTitle", "Quản lý Danh mục");
		return "admin/categories";
	}

	@GetMapping("/discounts")
	public String discounts(Model model) {
		model.addAttribute("pageTitle", "Chiết khấu App");
		return "admin/discounts";
	}

	@GetMapping("/promotions")
	public String promotions(Model model) {
		model.addAttribute("pageTitle", "Quản lý Khuyến mãi");
		return "admin/promotions";
	}

	@GetMapping("/shippers")
	public String shippers(Model model) {
		model.addAttribute("pageTitle", "Quản lý Nhà vận chuyển");
		return "admin/shippers";
	}

	@GetMapping("/settings")
	public String settings(Model model) {
		model.addAttribute("pageTitle", "Cài đặt");
		return "admin/settings";
	}

}
