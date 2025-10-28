package mocmien.com.controller.customer;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mocmien.com.entity.Cart;
import mocmien.com.entity.CartItem;
import mocmien.com.entity.User;
import mocmien.com.service.CartService;
import mocmien.com.service.UserService;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired private CartService cartService;
    @Autowired private UserService userService;

    /** 🛒 Trang xem giỏ hàng */
    @GetMapping
    public String viewCart(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<CartItem> items = cartService.getCartByUser(user);
        double total = cartService.getTotal(user);

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "customer/cart";
    }

    /** 🔄 Cập nhật số lượng */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateQuantity(
            @RequestParam("id") Integer itemId,
            @RequestParam("quantity") Integer quantity) {

        try {
            cartService.updateQuantity(itemId, quantity);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi cập nhật giỏ hàng");
        }
    }

    /** ❌ Xóa sản phẩm khỏi giỏ */
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<String> removeItem(@RequestParam("id") Integer itemId) {
        try {
            cartService.removeItem(itemId);
            return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi khi xóa sản phẩm");
        }
    }

    /** 🔢 Lấy số lượng sản phẩm trong giỏ (hiện badge ở header) */
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartCount(Principal principal) {
        try {
            int count = 0;
            if (principal != null) {
                User user = userService.findByUsername(principal.getName()).orElse(null);
                if (user != null)
                    count = cartService.getCartCount(user);
            }
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("count", 0));
        }
    }
}
