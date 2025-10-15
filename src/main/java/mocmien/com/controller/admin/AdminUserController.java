package mocmien.com.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminUserController {
	@GetMapping("/users")
	public String users(Model model) {
		model.addAttribute("pageTitle", "Quản lý User");
		return "admin/users/list";
	}
}
