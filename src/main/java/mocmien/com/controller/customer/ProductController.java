package mocmien.com.controller.customer;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mocmien.com.service.CartService;
import mocmien.com.service.ProductService;
import mocmien.com.service.UserService;
import mocmien.com.dto.response.product.ProductDetailResponse;
import mocmien.com.entity.Product;
import mocmien.com.entity.User;

@Controller
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductService productService;
	@Autowired 
	private CartService cartService;
    @Autowired 
    private UserService userService;
	
	@GetMapping("/{id}")
	public String productDetail(@PathVariable("id") Integer id, Model model) {
        ProductDetailResponse product = productService.getProductDetailById(id);
        if (product == null) {
            return "redirect:/product?notfound=true";
        }
        model.addAttribute("product", product);
        return "customer/product-detail";
    }


	@PostMapping("/add-to-cart")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<String> addToCart(@RequestParam("productId") Integer productId,
			@RequestParam("quantity") Integer quantity, Principal principal) {

		if (principal == null) {
			return ResponseEntity.status(401).body("Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng!");
		}

		User user = userService.findByUsername(principal.getName())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
		cartService.addToCart(user, productId, quantity);
		return ResponseEntity.ok("Đã thêm sản phẩm vào giỏ hàng!");
	}
}
