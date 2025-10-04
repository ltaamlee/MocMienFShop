package mocmien.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mocmien.com.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	Category findByCategoryName(String categoryName);
}
