package mocmien.com.controller.customer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import mocmien.com.dto.response.store.ShopDetailResponse;
import mocmien.com.entity.Product;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;
import mocmien.com.repository.ProductRepository;
import mocmien.com.repository.UserProfileRepository;
import mocmien.com.service.StoreService;


@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

	@Autowired
    private StoreService storeService;
	@Autowired
    private ProductRepository productRepository;
	@Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Trang chi tiết shop
     */
    @GetMapping("/{storeId}")
    public String viewShop(@PathVariable Integer storeId, Model model, Authentication authentication) {
        
        // Load user info for header
        addUserToModel(model, authentication);
        
        // Tìm store
        Store store = storeService.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shop"));
        
        // Lấy danh sách sản phẩm của shop
        List<Product> products = productRepository.findByStore(store);
        
        // Build response
        ShopDetailResponse response = new ShopDetailResponse();
        response.setStoreId(store.getId());
        response.setStoreName(store.getStoreName());
        response.setAvatar(store.getAvatar() != null ? store.getAvatar() : "/styles/image/default-shop.jpg");
        response.setCover(store.getCover() != null ? store.getCover() : "/styles/image/default-banner.jpg");
        response.setAddress(store.getAddress());
        response.setRating(store.getRating());
        response.setTotalProducts(products.size());
        response.setPhone(store.getPhone());
        response.setIsOpen(store.isOpen());
        
        // Vendor info
        if (store.getVendor() != null) {
            response.setVendorId(store.getVendor().getUserId());
            UserProfile profile = store.getVendor().getUserProfile();
            response.setVendorName(profile != null ? profile.getFullName() : store.getVendor().getUsername());
        }
        
        // Feature images (banner carousel)
        response.setFeatureImages(store.getFeatureImages());
        
        // Map products
        List<ShopDetailResponse.ShopProductDTO> productDTOs = products.stream()
                .filter(p -> p.getIsActive() != null && p.getIsActive())
                .map(p -> {
                    ShopDetailResponse.ShopProductDTO dto = new ShopDetailResponse.ShopProductDTO();
                    dto.setId(p.getId());
                    dto.setProductName(p.getProductName());
                    dto.setSlug(p.getSlug());
                    dto.setPrice(p.getPrice());
                    dto.setPromotionalPrice(p.getPromotionalPrice());
                    
                    // Main image
                    String mainImage = (p.getImages() != null && !p.getImages().isEmpty())
                            ? p.getImages().stream()
                                .filter(img -> Boolean.TRUE.equals(img.getIsDefault()))
                                .map(img -> img.getImageUrl())
                                .findFirst()
                                .orElse("/styles/image/default.jpg")
                            : "/styles/image/default.jpg";
                    dto.setMainImage(mainImage);
                    
                    dto.setRating(p.getRating());
                    dto.setSoldCount(p.getSold());
                    dto.setIsAvailable(p.getIsAvailable());
                    dto.setIsSelling(p.getIsSelling());
                    
                    return dto;
                })
                .collect(Collectors.toList());
        
        response.setProducts(productDTOs);
        
        model.addAttribute("shop", response);
        
        return "customer/shop-detail";
    }
    
    /**
     * Helper: add user info to model
     */
    private void addUserToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof mocmien.com.security.CustomUserDetails) {
                mocmien.com.security.CustomUserDetails userDetails = 
                    (mocmien.com.security.CustomUserDetails) principal;
                User user = userDetails.getUser();
                model.addAttribute("user", user);
                
                UserProfile profile = userProfileRepository.findById(user.getUserId()).orElse(null);
                if (profile != null) {
                    model.addAttribute("userProfile", profile);
                }
            }
        }
    }
}

