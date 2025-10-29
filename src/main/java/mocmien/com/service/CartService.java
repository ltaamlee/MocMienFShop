package mocmien.com.service;

import java.util.List;

import mocmien.com.entity.Cart;
import mocmien.com.entity.User;
import mocmien.com.entity.Product;
import mocmien.com.entity.CartItem;

public interface CartService {

	void addToCart(User user, Integer productId, Integer quantity);

	int getCartCount(User user);

	List<CartItem> getCartByUser(User user);

	void updateQuantity(Integer cartItemId, Integer quantity);

	void removeItem(Integer cartItemId);
	
	double getTotal(User user);
}
