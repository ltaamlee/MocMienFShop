package mocmien.com.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/category")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {
	@GetMapping
	public String users(Model model) {
		model.addAttribute("pageTitle", "Quản lý User");
		return "admin/users/list";
	}
}
