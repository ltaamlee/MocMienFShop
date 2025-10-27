package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.entity.Category;
import mocmien.com.entity.Product;
import mocmien.com.entity.Store;
import mocmien.com.repository.ProductRepository;
import mocmien.com.service.ProductService;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ===================== CƠ BẢN =====================
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> getBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    // ===================== LỌC / TÌM KIẾM =====================
    @Override
    public List<Product> searchByName(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> findByStore(Store store) {
        return productRepository.findByStore(store);
    }

    @Override
    public List<Product> findByStoreAndCategory(Store store, Category category) {
        return productRepository.findByStoreAndCategory(store, category);
    }

    @Override
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    // ===================== PHÂN TRANG =====================
    @Override
    public Page<Product> searchByName(String keyword, Pageable pageable) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public Page<Product> findByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Product> findByStore(Store store, Pageable pageable) {
        return productRepository.findByStore(store, pageable);
    }

    @Override
    public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    // ===================== DANH SÁCH NỔI BẬT =====================
    @Override
    public List<Product> getTopRated(BigDecimal minRating) {
        return productRepository.findTopRated(minRating);
    }

    @Override
    public List<Product> getTopSelling(int limit) {
        return productRepository.findTopSelling(limit);
    }

    @Override
    public List<Product> getDiscountedProducts() {
        return productRepository.findDiscountedProducts();
    }

    // ===================== TÌM KIẾM NÂNG CAO =====================
    @Override
    public List<Product> searchAdvanced(String keyword, Category category, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.searchAdvanced(keyword, category, minPrice, maxPrice);
    }

    // ===================== THỐNG KÊ =====================
    @Override
    public long countDiscounted() {
        return productRepository.countDiscountedProducts();
    }

    @Override
    public long countOutOfStock() {
        return productRepository.countOutOfStock();
    }

    @Override
    public BigDecimal averageRatingByStore(Store store) {
        return productRepository.averageRatingByStore(store);
    }
}
