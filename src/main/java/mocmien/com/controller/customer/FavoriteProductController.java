package mocmien.com.controller.customer;

import lombok.RequiredArgsConstructor;
import mocmien.com.entity.User;
import mocmien.com.service.FavoriteProductService;
import mocmien.com.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FavoriteProductController {

	@Autowired
    private FavoriteProductService favoriteProductService;
	@Autowired
    private UserService userService;

    @GetMapping("/favorites")
    public String favorites(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            model.addAttribute("favorites", favoriteProductService.getFavoriteProducts(user));
        } else {
            model.addAttribute("favorites", java.util.Collections.emptyList());
        }
        return "customer/favorite-products";
    }
}
