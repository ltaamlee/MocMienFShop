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
}