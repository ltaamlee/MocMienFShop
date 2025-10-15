package mocmien.com.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminStoreController {
	
	@GetMapping("/stores")
	public String stores(Model model) {
		model.addAttribute("pageTitle", "Quản lý cửa hàng");
		return "admin/stores/list";
	}
}
