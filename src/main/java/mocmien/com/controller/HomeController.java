package mocmien.com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

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
    
    @GetMapping("/contact")
    public String contact() {
        return "customer/contact";
    }
    
    @GetMapping("/about")
    public String about() {
        return "customer/about";
    }
    
}

