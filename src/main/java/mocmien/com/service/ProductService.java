package mocmien.com.service;

import java.util.List;
import java.util.Optional;

import mocmien.com.entity.Product;

public interface ProductService {
	
    List<Product> getAllProducts();

    Product getProductById(Integer id);

    Optional<Product> getProductByCode(String code);

    Product createProduct(Product product);

    Product updateProduct(Integer id, Product product);

    void deleteProduct(Integer id);

    boolean existsById(Integer id);
}
