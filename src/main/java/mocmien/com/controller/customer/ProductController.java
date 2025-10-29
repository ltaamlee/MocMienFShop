package mocmien.com.controller.customer;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mocmien.com.enums.RoleName;
import mocmien.com.dto.response.product.ProductDetailResponse;
import mocmien.com.entity.Review;
import mocmien.com.entity.User;
import mocmien.com.security.CustomUserDetails;
import mocmien.com.service.CartService;
import mocmien.com.service.ProductService;
import mocmien.com.service.ReviewService;
import mocmien.com.service.UserService;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private CartService cartService;
    @Autowired private UserService userService;
    @Autowired private ReviewService reviewService;

    // 🔹 Trang chi tiết sản phẩm
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        ProductDetailResponse product = productService.getProductDetailById(id);
        if (product == null) {
            return "redirect:/home?notfound=true";
        }

        model.addAttribute("product", product);
        model.addAttribute("title", product.getProductName()); // ✅ để <title> hoạt động

        List<Review> reviews = reviewService.getReviewsByProductId(id);
        model.addAttribute("reviews", reviews);
        // (Tuỳ chọn: nếu bạn có review hoặc sản phẩm liên quan)
        // model.addAttribute("reviews", reviewService.getByProductId(id));
        // model.addAttribute("relatedProducts", productService.getTopSelling(4));

        return "customer/product-detail";
    }

    // 🔹 API thêm vào giỏ hàng
    @PostMapping("/add-to-cart")
    @ResponseBody
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> addToCart(
            @RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng!"));
        }

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        cartService.addToCart(user, productId, quantity);
        return ResponseEntity.ok(Map.of("message", "✅ Đã thêm sản phẩm vào giỏ hàng!"));
    }
    
    @GetMapping("/buy-now/{id}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String buyNow(@PathVariable Integer id,
	                     @RequestParam(defaultValue = "1") Integer quantity) {
	    return "redirect:/checkout?productId=" + id + "&quantity=" + quantity;
	}
    

	@GetMapping("/check-buy")
	@ResponseBody
	public ResponseEntity<String> checkBuyPermission(Principal principal) {
		if (principal == null) {
			return ResponseEntity.status(401).body("Bạn cần đăng nhập để mua hàng!");
		}

		User user = userService.findByUsername(principal.getName())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

		// 🔹 So sánh bằng enum trực tiếp
		if (user.getRole().getRoleName() != RoleName.CUSTOMER) {
			return ResponseEntity.status(403).body("Tài khoản của bạn không được phép mua hàng!");
		}

		return ResponseEntity.ok("OK");
	}
}
