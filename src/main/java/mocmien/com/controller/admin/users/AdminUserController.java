package mocmien.com.controller.admin.users;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
	@GetMapping
	public String users(Model model) {
		model.addAttribute("pageTitle", "Quản lý User");
		return "admin/users/list";
	}
}
