package mocmien.com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import mocmien.com.entity.Product;
import mocmien.com.repository.ProductRepository;
import mocmien.com.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {
	
	private final ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Product getProductById(Integer id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
	}

	@Override
	public Optional<Product> getProductByCode(String code) {
		return Optional.ofNullable(productRepository.findByProductCode(code));
	}

	@Override
	public Product createProduct(Product product) {
		if (productRepository.findByProductCode(product.getProductCode()) != null) {
			throw new RuntimeException("Product already exists with code: " + product.getProductCode());
		}
		return productRepository.save(product);
	}

	@Override
	public Product updateProduct(Integer id, Product product) {
		Product existing = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

		existing.setProductName(product.getProductName());
		existing.setDescription(product.getDescription());
		existing.setPrice(product.getPrice());
		existing.setStock(product.getStock());
		existing.setStatus(product.getStatus());
		existing.setImageUrl(product.getImageUrl());
		existing.setCategory(product.getCategory());
		existing.setUpdatedAt(product.getUpdatedAt());

		return productRepository.save(existing);
	}

	@Override
	public void deleteProduct(Integer id) {
		if (!productRepository.existsById(id)) {
			throw new RuntimeException("Product not found with id: " + id);
		}
		productRepository.deleteById(id);
	}

	@Override
	public boolean existsById(Integer id) {
		return productRepository.existsById(id);
	}
}
