package mocmien.com.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import mocmien.com.dto.request.promotion.AdminPromitonCreateRequest;
import mocmien.com.dto.response.admin.AdminPromotionStats;
import mocmien.com.dto.response.promotion.AdminPromotionResponse;
import mocmien.com.entity.Promotion;

public interface AdminPromotionService {

	AdminPromotionStats getPromotionStatistics();

	Promotion createPromotion(@Valid AdminPromitonCreateRequest request);

	Page<AdminPromotionResponse> getPromotions(Pageable pageable, String keyword, String type, String status, String fromDate,
			String toDate);

}
