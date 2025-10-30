package mocmien.com.repository;

import mocmien.com.entity.FavoriteProduct;
import mocmien.com.entity.Product;
import mocmien.com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
	List<FavoriteProduct> findAllByUser(User user);

	boolean existsByUserAndProduct(User user, Product product);

	void deleteByUserAndProduct(User user, Product product);
}
