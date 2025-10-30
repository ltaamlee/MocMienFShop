package mocmien.com.service.impl;

import mocmien.com.dto.request.promotion.AdminPromitonCreateRequest;
import mocmien.com.dto.response.admin.AdminPromotionStats;
import mocmien.com.dto.response.promotion.AdminPromotionResponse;
import mocmien.com.dto.response.promotion.AdminStorePromotionResponse;
import mocmien.com.entity.Promotion;
import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;
import mocmien.com.repository.AdminPromotionRepository;
import mocmien.com.repository.PromotionRepository;
import mocmien.com.service.AdminPromotionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminPromotionServiceImpl implements AdminPromotionService {

	@Autowired
	private AdminPromotionRepository adminPromotionRepo;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public AdminPromotionStats getPromotionStatistics() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime soon = now.plusDays(3);

		long total = adminPromotionRepo.count();
		long active = adminPromotionRepo.countByStatus(PromotionStatus.ACTIVE);
		long upcoming = adminPromotionRepo.countUpcoming(now);
		long expired = adminPromotionRepo.countExpired(now);
		long expiring = adminPromotionRepo.countExpiring(now, soon);

		return new AdminPromotionStats(total, active, upcoming, expiring, expired);
	}

	@Override
	public Promotion createPromotion(@Valid AdminPromitonCreateRequest request) {
		if (adminPromotionRepo.existsByName(request.getName())) {
			throw new IllegalArgumentException("Tên khuyến mãi đã tồn tại.");
		}

		if (request.getStartDate().isAfter(request.getEndDate())) {
			throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc.");
		}

		// 2. Map DTO sang Entity
		Promotion promotion = new Promotion();

		promotion.setName(request.getName());
		promotion.setType(request.getType());
		promotion.setValue(request.getValue());
		promotion.setBanner(request.getBanner());

		promotion.setStartDate(request.getStartDate());
		promotion.setEndDate(request.getEndDate());

		promotion.setCreatedAt(LocalDateTime.now());
		// 3. Lưu vào CSDL
		return adminPromotionRepo.save(promotion);

	}

	@Override
	public Page<AdminPromotionResponse> getPromotions(Pageable pageable, String keyword, String type, String status,
			String fromDate, String toDate) {

        Specification<Promotion> spec = createFilterSpecification(keyword, type, status, fromDate, toDate)
                .and((root, query, builder) -> builder.isNull(root.get("store")));

		Page<Promotion> promotionEntities = adminPromotionRepo.findAll(spec, pageable);

		// 3. Chuyển đổi Page<Entity> sang Page<Response DTO>
		return promotionEntities.map(this::mapToAdminPromotionResponse);
	}

    @Override
    public Page<AdminStorePromotionResponse> getStorePromotions(Pageable pageable, String keyword, String type,
            String status, String fromDate, String toDate, Integer storeId) {

        Specification<Promotion> spec = createFilterSpecification(keyword, type, status, fromDate, toDate)
                .and((root, query, builder) -> builder.isNotNull(root.get("store")));

        if (storeId != null) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("store").get("id"), storeId));
        }

        Page<Promotion> promotionEntities = adminPromotionRepo.findAll(spec, pageable);

        return promotionEntities.map(this::mapToAdminStorePromotionResponse);
    }

    @Override
    public void banPromotion(Integer id) {
        Promotion p = adminPromotionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Khuyến mãi không tồn tại"));
        p.setStatus(PromotionStatus.BANNED);
        p.setIsActive(false);
        adminPromotionRepo.save(p);
    }

    @Override
    public void unbanPromotion(Integer id) {
        Promotion p = adminPromotionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Khuyến mãi không tồn tại"));
        if (p.getStatus() == PromotionStatus.BANNED) {
            // Khi bỏ cấm, đưa về INACTIVE để vendor có thể bật lại
            p.setStatus(PromotionStatus.INACTIVE);
            adminPromotionRepo.save(p);
        }
    }

    @Override
    public void activate(Integer id) {
        Promotion p = adminPromotionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Khuyến mãi không tồn tại"));
        if (p.getStatus() == PromotionStatus.BANNED) {
            throw new IllegalStateException("Khuyến mãi đã bị cấm, không thể kích hoạt");
        }
        // Chỉ cho ACTIVE nếu đang trong khung thời gian hợp lệ
        LocalDateTime now = LocalDateTime.now();
        if (p.getStartDate() != null && p.getEndDate() != null && !now.isBefore(p.getStartDate()) && !now.isAfter(p.getEndDate())) {
            p.setStatus(PromotionStatus.ACTIVE);
            p.setIsActive(true);
        } else if (p.getStartDate() != null && now.isBefore(p.getStartDate())) {
            p.setStatus(PromotionStatus.SCHEDULED);
            p.setIsActive(true);
        } else {
            p.setStatus(PromotionStatus.EXPIRED);
            p.setIsActive(false);
        }
        adminPromotionRepo.save(p);
    }

    @Override
    public void deactivate(Integer id) {
        Promotion p = adminPromotionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Khuyến mãi không tồn tại"));
        p.setStatus(PromotionStatus.INACTIVE);
        p.setIsActive(false);
        adminPromotionRepo.save(p);
    }

	// --- Logic tạo Specification ---
	private Specification<Promotion> createFilterSpecification(String keyword, String type,  String status, String fromDate,
			String toDate) {

		return (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			LocalDateTime now = LocalDateTime.now();

			if (StringUtils.hasText(keyword)) {
				String likeKeyword = "%" + keyword.toLowerCase() + "%";
				predicates.add(builder.like(builder.lower(root.get("name")), likeKeyword));
			}


			if (StringUtils.hasText(type)) {
				try {
					PromotionType promotionType = PromotionType.valueOf(type.toUpperCase());
					predicates.add(builder.equal(root.get("type"), promotionType));
				} catch (IllegalArgumentException e) {
					// Bỏ qua nếu type không hợp lệ
				}
			}


			if (StringUtils.hasText(status)) {
				try {
					PromotionStatus targetStatus = PromotionStatus.valueOf(status.toUpperCase());

					if (targetStatus == PromotionStatus.ACTIVE) {
						predicates.add(builder.lessThanOrEqualTo(root.get("startDate"), now));
						predicates.add(builder.greaterThanOrEqualTo(root.get("endDate"), now));
					} else if (targetStatus == PromotionStatus.SCHEDULED) {
						// Sắp bắt đầu: start > now
						predicates.add(builder.greaterThan(root.get("startDate"), now));
					} else if (targetStatus == PromotionStatus.EXPIRED) {
						// Đã hết hạn: end < now
						predicates.add(builder.lessThan(root.get("endDate"), now));
					}
				} catch (IllegalArgumentException e) {
					// Bỏ qua nếu status không hợp lệ
				}
			}

			// 4. Lọc theo Ngày bắt đầu (fromDate)
			if (StringUtils.hasText(fromDate)) {
			    try {
			        // Sử dụng LocalDate.parse an toàn hơn và thêm 00:00:00
			        LocalDateTime startDateTime = java.time.LocalDate.parse(fromDate).atStartOfDay();
			        predicates.add(builder.greaterThanOrEqualTo(root.get("startDate"), startDateTime));
			    } catch (Exception e) {}
			}

			// 5. Lọc theo Ngày kết thúc (toDate)
			if (StringUtils.hasText(toDate)) {
			    try {
			        // Sử dụng LocalDate.parse an toàn hơn và thêm 23:59:59
			        LocalDateTime endDateTime = java.time.LocalDate.parse(toDate).plusDays(1).atStartOfDay();
			        predicates.add(builder.lessThanOrEqualTo(root.get("endDate"), endDateTime));
			    } catch (Exception e) {}
			}

			// Trả về kết quả kết hợp
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}

	// --- Phương thức mapping DTO (Giữ nguyên sau khi fix lỗi null) ---
	private AdminPromotionResponse mapToAdminPromotionResponse(Promotion promotion) {
		if (promotion == null) {
			return new AdminPromotionResponse();
		}

		AdminPromotionResponse response = new AdminPromotionResponse();
		response.setId(promotion.getId());
		response.setName(promotion.getName());
		response.setType(promotion.getType());
		response.setValue(promotion.getValue());
		response.setBanner(promotion.getBanner());
		// Giả định Entity Promotion có trường ribbon
		// response.setRibbon(promotion.getRibbon());

		response.setStartDate(promotion.getStartDate());
		response.setEndDate(promotion.getEndDate());
		response.setCreatedAt(promotion.getCreatedAt());

		// Logic Status (Tính toán trạng thái)
		response.setStatus(determineStatus(promotion));

		return response;
	}

	// --- Phương thức xác định Status (Giữ nguyên) ---
	private PromotionStatus determineStatus(Promotion promotion) { 
	    
	    // 1. Kiểm tra trạng thái INACTIVE cố định trước tiên
	    if (promotion.getStatus() == PromotionStatus.INACTIVE) {
	        return PromotionStatus.INACTIVE;
	    }

	    // 2. Tiếp tục logic dựa trên ngày tháng (cho ACTIVE, SCHEDULED, EXPIRED)
	    LocalDateTime now = LocalDateTime.now();
	    LocalDateTime startDate = promotion.getStartDate();
	    LocalDateTime endDate = promotion.getEndDate();

	    if (startDate == null || endDate == null) {
	        return PromotionStatus.EXPIRED; // Trạng thái mặc định nếu thiếu ngày tháng
	    }

	    if (now.isBefore(startDate)) {
	        return PromotionStatus.SCHEDULED;
	    } else if (now.isAfter(endDate)) {
	        return PromotionStatus.EXPIRED;
	    } else {
	        return PromotionStatus.ACTIVE;
	    }
	}

    private AdminStorePromotionResponse mapToAdminStorePromotionResponse(Promotion promotion) {
        AdminStorePromotionResponse resp = new AdminStorePromotionResponse();
        resp.setId(promotion.getId());
        resp.setName(promotion.getName());
        resp.setType(promotion.getType());
        resp.setValue(promotion.getValue());
        resp.setStartDate(promotion.getStartDate());
        resp.setEndDate(promotion.getEndDate());
        resp.setStatus(promotion.getStatus() == null ? determineStatus(promotion) : promotion.getStatus());
        if (promotion.getStore() != null) {
            resp.setStoreId(promotion.getStore().getId());
            resp.setStoreName(promotion.getStore().getStoreName());
        }
        return resp;
    }

}
