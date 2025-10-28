package mocmien.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Cart;
import mocmien.com.entity.CartItem;
import mocmien.com.entity.Product;
import mocmien.com.entity.User;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    List<CartItem> findByCart(Cart cart);
    
    List<CartItem> findByCart_User(User user);
    
    int countByCart_User(User user);
}
