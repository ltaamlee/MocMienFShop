package mocmien.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Flower;
import mocmien.com.entity.Product;
import mocmien.com.entity.ProductFlower;

@Repository
public interface ProductFlowerRepository extends JpaRepository<ProductFlower, Integer> {

    // -----------------------
    // CRUD cơ bản
    // -----------------------
    ProductFlower save(ProductFlower pf);
    void deleteById(Integer id);

    // -----------------------
    // Tìm kiếm
    // -----------------------
    List<ProductFlower> findByProduct(Product product);
    List<ProductFlower> findByFlower(Flower flower);

    List<ProductFlower> findByProductAndIsActiveTrue(Product product);

    // -----------------------
    // Thống kê / đếm
    // -----------------------
    long countByProduct(Product product);
    long countByFlower(Flower flower);
}