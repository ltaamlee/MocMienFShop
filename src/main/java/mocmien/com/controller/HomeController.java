package mocmien.com.controller;


import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import mocmien.com.service.ProductService;
import mocmien.com.service.UserService;
import mocmien.com.dto.product.ProductRowVM;
import mocmien.com.entity.Category;
import mocmien.com.entity.User;
import mocmien.com.repository.CategoryRepository;


@Controller
public class HomeController {
	
	private final UserService userService;
	private final ProductService productService;
	private final CategoryRepository categoryRepository;

	public HomeController(UserService userService, ProductService productService, CategoryRepository categoryRepository) {
		this.userService = userService;
		this.productService = productService;
		this.categoryRepository = categoryRepository;
	}

	@GetMapping("/")
	public String index(Model model, Authentication authentication) {
		addUserToModel(model, authentication);
		model.addAttribute("title", "MocMien Flower Shop");
		return "index";
	}

	@GetMapping("/home")
	public String guestHome(Model model, Authentication authentication) {
	    addUserToModel(model, authentication);
	    model.addAttribute("title", "MocMien Flower Shop");

	    // ✅ Lấy danh sách sản phẩm hiển thị ra trang home
	    List<ProductRowVM> products = productService.getAllProductRows();
	    model.addAttribute("products", products);

	    return "customer/home";
	}

	
	@GetMapping("/product")
	public String showProducts(
	        @RequestParam(value = "q", required = false) String keyword,
	        @RequestParam(value = "sort", required = false) String sort,
	        @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
	        Model model,
	        Authentication authentication) {
	    addUserToModel(model, authentication);

	    List<Category> categories = categoryRepository.findByIsActiveTrueOrderByCategoryNameAsc();
	    model.addAttribute("categories", categories);

	    // ✅ Xử lý filter (nếu chưa chọn category nào thì set rỗng)
	    model.addAttribute("selectedCategories",
	            categoryIds != null ? categoryIds : List.of());

	    // ✅ Giữ lại keyword và sort
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("sort", sort);

	    // ✅ Lấy danh sách sản phẩm theo filter + sort
	    List<ProductRowVM> products = productService.getAllProductRows(); // hoặc service filter theo categoryIds/sort
	    model.addAttribute("products", products);

	    return "customer/product";
	}


	@GetMapping("/contact")
	public String contact(Model model, Authentication authentication) {
		addUserToModel(model, authentication);
		return "customer/contact";
	}

	@GetMapping("/about")
	public String about(Model model, Authentication authentication) {
		addUserToModel(model, authentication);
		return "customer/about";
	}

	@GetMapping("/return-policy")
	public String returnPolicy(Model model, Authentication authentication) {
		addUserToModel(model, authentication);
		return "customer/return-policy";
	}

	@GetMapping("/delivery-policy")
	public String deliveryPolicy(Model model, Authentication authentication) {
		addUserToModel(model, authentication);
		return "customer/delivery-policy";
	}

	@GetMapping("/payment-guide")
	public String paymentGuide(Model model, Authentication authentication) {
		addUserToModel(model, authentication);
		return "customer/payment-guide";
	}

	@ModelAttribute("user")
	private void addUserToModel(Model model, Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			User user = userService.findByUsername(username).orElse(null);
			model.addAttribute("user", user);
		} else {
			model.addAttribute("user", null);
		}
	}
	
}
