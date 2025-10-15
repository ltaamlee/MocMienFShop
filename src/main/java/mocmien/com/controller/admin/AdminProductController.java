package mocmien.com.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

	@GetMapping("/products")
	public String products(Model model) {
		model.addAttribute("pageTitle", "Quản lý Sản phẩm");
		return "admin/products";
	}
}
