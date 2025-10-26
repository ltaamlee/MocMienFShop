package mocmien.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import mocmien.com.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	// -----------------------
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

    // -----------------------
    // Tìm kiếm nâng cao
    // -----------------------
    Optional<Category> findBySlug(String slug);
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


/*
 * List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "categoryName"));
	List<Category> activeCategories = categoryRepository.findByIsActive(true, Sort.by("createdAt").descending());
 * */
