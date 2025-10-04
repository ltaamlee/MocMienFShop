package mocmien.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mocmien.com.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{
	Product findByProductCode(String productCode);
}
