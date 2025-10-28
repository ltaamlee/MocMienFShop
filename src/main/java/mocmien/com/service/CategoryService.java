package mocmien.com.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mocmien.com.dto.request.CategoryRequest;
import mocmien.com.dto.response.category.CategoryResponse;
import mocmien.com.dto.response.category.CategoryStats;
import mocmien.com.entity.Category;

public interface CategoryService {
	List<CategoryResponse> getAllCategories();

	CategoryResponse getCategoryById(Integer id);

	CategoryResponse saveCategory(CategoryRequest request);

	void deleteCategory(Integer id);

	Page<CategoryResponse> findPaginated(String keyword, Boolean isActive, LocalDate fromDate, LocalDate toDate,
			Pageable pageable);

	CategoryStats getCategoryStatistics();

	void toggleCategoryStatus(Integer id);

}
