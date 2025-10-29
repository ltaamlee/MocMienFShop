package mocmien.com.controller.admin;
import java.text.Normalizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // <-- 1. THÊM IMPORT NÀY
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import mocmien.com.dto.request.CategoryRequest;
import mocmien.com.dto.response.category.CategoryResponse;
import mocmien.com.dto.response.category.CategoryStats;
import mocmien.com.service.CategoryService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin/category")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/stats")
	public CategoryStats getCategoryStatistics() {
		return categoryService.getCategoryStatistics();
	}

	/**
	 * 🔹 HÀM NÀY ĐÃ ĐƯỢC CẬP NHẬT ĐỂ NHẬN VÀ XỬ LÝ 'SORT'
	 */
	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllCategories(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Boolean isActive,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "10") int size,
			
			// --- 2. THÊM THAM SỐ SORT ---
			@RequestParam(defaultValue = "createdAt,desc") String sort 
	) {

		// --- 3. THÊM LOGIC XỬ LÝ SORT ---
		Pageable pageable;
		try {
			// Tách chuỗi "field,direction"
			String[] sortParams = sort.split(",");
			String sortField = sortParams[0];
			Sort.Direction sortDirection = sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
			pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
		} catch (Exception e) {
			// Mặc định nếu param 'sort' bị sai
			pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		}
		// --- KẾT THÚC LOGIC SORT ---

		Page<CategoryResponse> categoryPage = categoryService.findPaginated(keyword, isActive, fromDate, toDate,
				pageable); // `pageable` giờ đã chứa thông tin sort

		Map<String, Object> response = new HashMap<>();
		response.put("content", categoryPage.getContent());
		response.put("currentPage", categoryPage.getNumber());
		response.put("totalItems", categoryPage.getTotalElements());
		response.put("totalPages", categoryPage.getTotalPages());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public CategoryResponse getCategoryById(@PathVariable Integer id) {
		return categoryService.getCategoryById(id);
	}

	@PostMapping
	public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) { 
		try {
            prepareSlugAndStatus(request); 
            CategoryResponse response = categoryService.saveCategory(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED); 
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody CategoryRequest request) { 
		try {
            request.setId(id);
            prepareSlugAndStatus(request);
            CategoryResponse response = categoryService.saveCategory(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
	}

	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
		try {
			categoryService.deleteCategory(id); 
			return ResponseEntity.ok(Map.of("message", "Xóa danh mục thành công!"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "Lỗi khi xóa danh mục: " + e.getMessage()));
		}
	}
	
	@PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleCategoryStatus(@PathVariable Integer id) {
        try {
            categoryService.toggleCategoryStatus(id); 
            return ResponseEntity.ok(Map.of("message", "Đổi trạng thái thành công!"));
        } catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi server khi đổi trạng thái: " + e.getMessage()));
        }
    }
	
	// --- Tiện ích ---

	/**
	 * 🔹 HÀM NÀY ĐÃ ĐƯỢC SỬA LỖI LOGIC (active vs isActive)
	 */
	private void prepareSlugAndStatus(CategoryRequest request) {
		if (request.getSlug() == null || request.getSlug().trim().isEmpty()) {
			request.setSlug(generateSlug(request.getCategoryName()));
		}
		
		if (request.getIsActive() == null) {
			request.setIsActive(false); 
		}
	}

	private String generateSlug(String input) {
		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
		String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase()
				.replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-").trim();
		return slug;
	}
}