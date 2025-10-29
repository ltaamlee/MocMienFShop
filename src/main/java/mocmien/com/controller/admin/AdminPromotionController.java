package mocmien.com.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mocmien.com.dto.request.promotion.AdminPromitonCreateRequest;
import mocmien.com.dto.response.admin.AdminPromotionStats;
import mocmien.com.dto.response.promotion.AdminPromotionResponse;
import mocmien.com.entity.Promotion;
import mocmien.com.service.AdminPromotionService;

@RestController
@RequestMapping("/api/admin/promotion")
public class AdminPromotionController {

	@Autowired
	private AdminPromotionService adminPromotionService;
	
	// -----------------------
	// 1. THỐNG KÊ KHUYẾN MÃI
	// -----------------------
	@GetMapping("/stats")
	public ResponseEntity<AdminPromotionStats> getPromotionStatistics() {
		AdminPromotionStats stats = adminPromotionService.getPromotionStatistics();
		return ResponseEntity.ok(stats);
	}

	// DANH SÁCH CÁC KHUYẾN MÃI CỦA ADMIN TẠO
	@GetMapping
    public ResponseEntity<Page<AdminPromotionResponse>> getPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt,desc") String sort, // e.g., "name,asc"
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        
        String[] sortParams = sort.split(",");
        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);

        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<AdminPromotionResponse> promotions = adminPromotionService.getPromotions(
                pageable, keyword, type, status, fromDate, toDate);

        return ResponseEntity.ok(promotions);
    }
	
	//THÊM KHUYẾN MÃI
	@PostMapping("/global")
    public ResponseEntity<?> createGlobalPromotion(
            @Valid @RequestBody AdminPromitonCreateRequest request) {

        try {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ngày bắt đầu phải trước ngày kết thúc.");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }

            Promotion newPromotion = adminPromotionService.createPromotion(request); 
            return new ResponseEntity<>(newPromotion, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi máy chủ khi tạo khuyến mãi: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
