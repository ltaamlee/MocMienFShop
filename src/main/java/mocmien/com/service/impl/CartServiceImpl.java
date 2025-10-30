package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.entity.Cart;
import mocmien.com.entity.CartItem;
import mocmien.com.entity.Product;
import mocmien.com.entity.User;
import mocmien.com.repository.CartItemRepository;
import mocmien.com.repository.CartRepository;
import mocmien.com.repository.ProductRepository;
import mocmien.com.service.CartService;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;

    // 🟢 1. Thêm sản phẩm vào giỏ
    @Override
    public void addToCart(User user, Integer productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Lấy hoặc tạo giỏ hàng mới cho user
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setStore(product.getStore());
                    return cartRepository.save(newCart);
                });

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ chưa
        Optional<CartItem> existing = cartItemRepository.findByCartAndProduct(cart, product);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    // 🟢 2. Đếm số loại sản phẩm trong giỏ (không phải tổng số lượng)
    @Override
    public int getCartCount(User user) {
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        if (cartOpt.isEmpty()) return 0;

        Cart cart = cartOpt.get();
        List<CartItem> items = cartItemRepository.findByCart(cart);
        return items.size(); // Đếm số loại sản phẩm, không phải tổng quantity
    }

    // 🟢 3. Lấy toàn bộ sản phẩm trong giỏ của user
    @Override
    public List<CartItem> getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .map(cart -> cartItemRepository.findByCart(cart))
                .orElse(List.of());
    }

    // 🟢 4. Cập nhật số lượng sản phẩm trong giỏ
    @Override
    public void updateQuantity(Integer cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    // 🟢 5. Xóa một sản phẩm khỏi giỏ
    @Override
    public void removeItem(Integer cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));
        cartItemRepository.delete(item);
    }
    
    @Override
    public double getTotal(User user) {
        List<CartItem> items = cartItemRepository.findByCart_User(user);
        return items.stream()
                .mapToDouble(i -> {
                    Product p = i.getProduct();
                    // Dùng giá khuyến mãi nếu có, ngược lại dùng giá gốc
                    BigDecimal unitPrice = (p.getPromotionalPrice() != null && p.getPromotionalPrice().compareTo(p.getPrice()) < 0)
                            ? p.getPromotionalPrice()
                            : p.getPrice();
                    return unitPrice.doubleValue() * i.getQuantity();
                })
                .sum();
    }
}
