package mocmien.com.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import mocmien.com.dto.request.CategoryRequest;
import mocmien.com.dto.response.category.CategoryResponse;
import mocmien.com.dto.response.category.CategoryStats;
import mocmien.com.entity.Category;
import mocmien.com.repository.CategoryRepository;
import mocmien.com.repository.ProductRepository;
import mocmien.com.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ProductRepository productRepository;

	@Override
	public CategoryStats getCategoryStatistics() {
		long total = categoryRepository.count();
		long active = categoryRepository.countByIsActive(true);
		long inactive = categoryRepository.countByIsActive(false);

		return new CategoryStats(total, active, inactive);
	}

	@Override
	public List<CategoryResponse> getAllCategories() {
		return categoryRepository.findAll().stream().map(this::toResponse).toList();
	}

	private void validateCategory(CategoryRequest request) {
		// 1. Kiểm tra trùng TÊN
		Optional<Category> existingByName = categoryRepository.findByCategoryName(request.getCategoryName());

		if (existingByName.isPresent()) {
			// Nếu là THÊM MỚI (Id == null) -> Chắc chắn trùng
			// Nếu là CẬP NHẬT (Id != null) -> Chỉ trùng khi ID tìm thấy KHÁC ID đang sửa
			if (request.getId() == null || !existingByName.get().getId().equals(request.getId())) {
				throw new IllegalArgumentException("Tên danh mục này đã tồn tại.");
			}
		}

		// 2. Kiểm tra trùng SLUG (tương tự)
		Optional<Category> existingBySlug = categoryRepository.findBySlug(request.getSlug());
		if (existingBySlug.isPresent()) {
			if (request.getId() == null || !existingBySlug.get().getId().equals(request.getId())) {
				throw new IllegalArgumentException("Slug này đã tồn tại.");
			}
		}
	}

	@Override
	public CategoryResponse saveCategory(CategoryRequest request) {

		validateCategory(request);

		Category category;
		if (request.getId() != null) {
			category = categoryRepository.findById(request.getId())
					.orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục để cập nhật"));
		} else {
			category = new Category();
		}

		category.setCategoryName(request.getCategoryName());
		category.setSlug(request.getSlug());

		category.setActive(request.getIsActive() != null ? request.getIsActive() : true);

		categoryRepository.save(category);
		return toResponse(category);
	}

	private CategoryResponse toResponse(Category category) {
		return new CategoryResponse(category.getId(), category.getCategoryName(), category.getSlug(),
				category.isActive(), category.getCreatedAt(), category.getUpdatedAt());
	}

	@Override
	public CategoryResponse getCategoryById(Integer id) {
		return categoryRepository.findById(id).map(this::toResponse).orElse(null);
	}

	@Override
	public void deleteCategory(Integer id) {
		long count = productRepository.countByCategory_Id(id);
		if (count > 0) {
			throw new IllegalStateException("Không thể xóa danh mục vì vẫn còn sản phẩm đang sử dụng.");
		}
		categoryRepository.deleteById(id);
	}

	@Override
	public Page<CategoryResponse> findPaginated(String keyword, Boolean isActive, LocalDate fromDate, LocalDate toDate,
			Pageable pageable) {

		// Tạo một Specification (bộ lọc động)
		Specification<Category> spec = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// Lọc theo Keyword (tìm ở Tên HOẶC Slug)
			if (keyword != null && !keyword.trim().isEmpty()) {
				String likePattern = "%" + keyword.trim().toLowerCase() + "%";
				Predicate nameLike = cb.like(cb.lower(root.get("categoryName")), likePattern);
				Predicate slugLike = cb.like(cb.lower(root.get("slug")), likePattern);
				predicates.add(cb.or(nameLike, slugLike));
			}

			// Lọc theo Trạng thái (dùng 'isActive' vì tên trường Entity là vậy)
			if (isActive != null) {
				predicates.add(cb.equal(root.get("isActive"), isActive));
			}

			// Lọc theo Ngày (dùng 'createdAt')
			if (fromDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate.atStartOfDay()));
			}
			if (toDate != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate.atTime(LocalTime.MAX)));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};

		Page<Category> categories = categoryRepository.findAll(spec, pageable);

		return categories.map(this::toResponse);
	}

	@Override

	public void toggleCategoryStatus(Integer id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục với ID: " + id));
		boolean currentStatus = Boolean.TRUE.equals(category.isActive());
		category.setActive(!currentStatus);
		categoryRepository.save(category);
	}

	@Override
	public List<Category> getActiveCategories() {
		List<Category> activeCategories = categoryRepository.findByIsActiveTrueOrderByCategoryNameAsc();

		return activeCategories;
	}

}
