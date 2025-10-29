package mocmien.com.service;

import java.util.List;

import org.springframework.data.domain.*;

import mocmien.com.dto.request.promotion.VendorPromotionCreateRequest;
import mocmien.com.dto.request.promotion.VendorPromotionUpdateRequest;
import mocmien.com.dto.response.promotion.VendorPromotionDetailResponse;
import mocmien.com.dto.response.promotion.VendorPromotionListItemResponse;
import mocmien.com.dto.response.promotion.VendorPromotionStatsResponse;
import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;

public interface VendorPromotionService {
	 List<mocmien.com.repository.PromotionRepository.IdName> productOptions(Integer vendorUserId);

	
	Page<VendorPromotionListItemResponse> list(Integer vendorUserId, String keyword, PromotionStatus status,
			PromotionType type, Pageable pageable);

	VendorPromotionStatsResponse stats(Integer vendorUserId);

	VendorPromotionDetailResponse detail(Integer vendorUserId, Integer id);

	Integer create(Integer vendorUserId, VendorPromotionCreateRequest req);

	VendorPromotionDetailResponse update(Integer vendorUserId, Integer id, VendorPromotionUpdateRequest req);

	void updateStatus(Integer vendorUserId, Integer id, PromotionStatus status);

	void delete(Integer vendorUserId, Integer id);
}