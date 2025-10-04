package mocmien.com.service;

import java.util.List;
import java.util.Optional;

import mocmien.com.entity.Category;

public interface CategoryService {

	List<Category> getAllCategories();

	Category getCategoryById(Integer id);

	Optional<Category> getCategoryByName(String name);

	Category createCategory(Category category);

	Category updateCategory(Integer id, Category category);

	void deleteCategory(Integer id);

	boolean existsById(Integer id);
}