package mocmien.com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import mocmien.com.entity.Category;
import mocmien.com.repository.CategoryRepository;
import mocmien.com.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public Category getCategoryById(Integer id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
	}

	@Override
	public Optional<Category> getCategoryByName(String name) {
		return Optional.ofNullable(categoryRepository.findByCategoryName(name));
	}

	@Override
	public Category createCategory(Category category) {
		if (categoryRepository.findByCategoryName(category.getCategoryName()) != null) {
			throw new RuntimeException("Category already exists with name: " + category.getCategoryName());
		}
		return categoryRepository.save(category);
	}

	@Override
	public Category updateCategory(Integer id, Category category) {
		Category existing = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

		existing.setCategoryName(category.getCategoryName());
		existing.setDescription(category.getDescription());
		existing.setUpdatedAt(category.getUpdatedAt());

		return categoryRepository.save(existing);
	}

	@Override
	public void deleteCategory(Integer id) {
		if (!categoryRepository.existsById(id)) {
			throw new RuntimeException("Category not found with id: " + id);
		}
		categoryRepository.deleteById(id);
	}

	@Override
	public boolean existsById(Integer id) {
		return categoryRepository.existsById(id);
	}
}