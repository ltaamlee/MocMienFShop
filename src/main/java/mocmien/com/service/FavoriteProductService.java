package mocmien.com.service;

import mocmien.com.entity.Product;
import mocmien.com.entity.User;
import java.util.List;

public interface FavoriteProductService {
    void addFavorite(User user, Product product);
    void removeFavorite(User user, Product product);
    boolean isFavorite(User user, Product product);
    List<Product> getFavoriteProducts(User user);
}
