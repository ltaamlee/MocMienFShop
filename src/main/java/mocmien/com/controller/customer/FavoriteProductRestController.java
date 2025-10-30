package mocmien.com.controller.customer;

import lombok.RequiredArgsConstructor;
import mocmien.com.entity.Product;
import mocmien.com.entity.User;
import mocmien.com.service.FavoriteProductService;
import mocmien.com.service.ProductService;
import mocmien.com.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite-products")
@RequiredArgsConstructor
public class FavoriteProductRestController {

	@Autowired
    private FavoriteProductService favoriteProductService;
	@Autowired
    private ProductService productService;
	@Autowired
    private UserService userService; // ✅ thêm dòng này

    @PostMapping("/add/{productId}")
    public ResponseEntity<Void> add(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer productId) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build(); // chưa đăng nhập
        }

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        favoriteProductService.addFavorite(user, product);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer productId) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        favoriteProductService.removeFavorite(user, product);
        return ResponseEntity.ok().build();
    }
}
