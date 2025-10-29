package mocmien.com.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mocmien.com.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {
	List<Category> findByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);

	long countByIsActive(boolean isActive);

	@Query("""
			    SELECT c FROM Category c
			    WHERE (:kw IS NULL OR LOWER(c.categoryName) LIKE %:kw%)
			    AND (:isActive IS NULL OR c.isActive = :isActive)
			    AND (:fromDate IS NULL OR c.createdAt >= :fromDate)
			    AND (:toDate IS NULL OR c.createdAt <= :toDate)
			    ORDER BY c.createdAt DESC
			""")
	Page<Category> searchCategories(@Param("kw") String keyword, @Param("isActive") Boolean isActive,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, Pageable pageable);

	Optional<Category> findByCategoryName(String categoryName);

	Optional<Category> findBySlug(String slug);

	// CRUD cơ bản
	// -----------------------
	@Override
	<S extends Category> S save(S category);

	@Override
	void delete(Category category);

	@Override
	void deleteById(Integer id);

	@Override
	Optional<Category> findById(Integer id);

	@Override
	List<Category> findAll();

	List<Category> findByIsActiveTrueOrderByCategoryNameAsc();

	// -----------------------
	// Tìm kiếm nâng cao
	// -----------------------

	List<Category> findByCategoryNameContainingIgnoreCase(String keyword);

	// -----------------------
	// Trạng thái active
	// -----------------------
	List<Category> findByIsActive(Boolean isActive);

	// -----------------------
	// Phân trang
	// -----------------------
	Page<Category> findByIsActive(Boolean isActive, Pageable pageable);

	Page<Category> findByCategoryNameContainingIgnoreCase(String keyword, Pageable pageable);

	// -----------------------
	// Kiểm tra tồn tại
	// -----------------------
	boolean existsByCategoryName(String categoryName);

	boolean existsBySlug(String slug);

	// -----------------------
	// Thống kê
	// -----------------------
	long countByIsActive(Boolean isActive);

	// -----------------------
	// Sắp xếp (Sort)
	// -----------------------
	List<Category> findAll(Sort sort); // Lấy tất cả category với sort

	List<Category> findByIsActive(Boolean isActive, Sort sort); // Lấy theo trạng thái và sort

	List<Category> findByCategoryNameContainingIgnoreCase(String keyword, Sort sort); // tìm kiếm và sort
}


