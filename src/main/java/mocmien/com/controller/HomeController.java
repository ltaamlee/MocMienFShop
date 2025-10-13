package mocmien.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mocmien.com.entity.User;
import mocmien.com.repository.UserRepository;


@Controller
public class HomeController {

	@Autowired
    private UserRepository userRepository;
	
	@GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "MocMien Flower Shop");
        return "index";
    }

    @GetMapping("/home")
    public String guestHome(Model model) {
        model.addAttribute("title", "Trang Guest Home");
        model.addAttribute("message", "Đây là trang Guest Home!");
        return "customer/home";
    }
    
    @GetMapping("/product")
    public String product(Model model, Authentication authentication) {
        addUserToModel(model, authentication);
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

    private void addUserToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }
    }
    
}

