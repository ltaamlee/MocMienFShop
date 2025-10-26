package mocmien.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Product;
import mocmien.com.entity.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    // -----------------------
    // CRUD cơ bản
    // -----------------------
    ProductImage save(ProductImage image);
    void deleteById(Integer id);

    // -----------------------
    // Tìm kiếm
    // -----------------------
    List<ProductImage> findByProduct(Product product);
    Optional<ProductImage> findByProductAndIsDefaultTrue(Product product);

    List<ProductImage> findByIsDefaultTrue();
    List<ProductImage> findByProductAndImageIndex(Integer imageIndex, Product product);

    // -----------------------
    // Thống kê / đếm
    // -----------------------
    long countByProduct(Product product);
}