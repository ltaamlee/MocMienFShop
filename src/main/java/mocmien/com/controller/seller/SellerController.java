package mocmien.com.controller.seller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/seller")
public class SellerController {
	@GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        return "seller/dashboard";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Đăng ký Shop");
        return "seller/register";           // templates/seller/register.html
    }

    @GetMapping("/shop")
    public String shop(Model model) {
        model.addAttribute("pageTitle", "Quản lý Shop");
        return "seller/shop";               // templates/seller/shop.html
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("pageTitle", "Quản lý Sản phẩm");
        return "seller/products";           // templates/seller/products.html
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("pageTitle", "Quản lý Đơn hàng");
        return "seller/orders";             // templates/seller/orders.html
    }

    @GetMapping("/promotions")
    public String promotions(Model model) {
        model.addAttribute("pageTitle", "Quản lý Khuyến mãi");
        return "seller/promotions";         // templates/seller/promotions.html
    }

    @GetMapping("/revenue")
    public String revenue(Model model) {
        model.addAttribute("pageTitle", "Quản lý Doanh thu");
        return "seller/revenue";            // templates/seller/revenue.html
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("pageTitle", "Cài đặt");
        return "seller/settings";           // templates/seller/settings.html
    }
}
