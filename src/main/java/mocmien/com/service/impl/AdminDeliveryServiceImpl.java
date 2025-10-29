package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import mocmien.com.dto.request.delivery.DeliveryRequest;
import mocmien.com.dto.response.admin.DeliveryResponse;
import mocmien.com.dto.response.admin.DeliveryStats;
import mocmien.com.entity.Delivery;
import mocmien.com.repository.DeliveryRepository;
import mocmien.com.service.AdminDeliveryService;

@Service
public class AdminDeliveryServiceImpl implements AdminDeliveryService {

	@Autowired
	private DeliveryRepository deliveryRepository;

	// ------------------------------------
	// THỐNG KÊ
	// ------------------------------------
	@Override
	public DeliveryStats getDeliveryStatistics() {
		long total = deliveryRepository.count();
		long active = deliveryRepository.countByIsActive(true);
		long inactive = deliveryRepository.countByIsActive(false);

		return new DeliveryStats(total, active, inactive);
	}

	// ------------------------------------
	// CRUD & PHÂN TRANG
	// ------------------------------------
	@Override
    public Page<DeliveryResponse> findAllDeliveries(Pageable pageable) {
        Page<Delivery> deliveryPage = deliveryRepository.findAll(pageable);
        return deliveryPage.map(this::toResponse);
    }

	@Override
	public Delivery findById(Integer id) {
		return deliveryRepository.findById(id).orElseThrow();
	}

	@Override
	@Transactional
	public Delivery save(Delivery delivery) {
		if (delivery.getIsActive() == null) {
			delivery.setIsActive(false);
		}
		if (delivery.getCreatedAt() == null) {
			delivery.setCreatedAt(LocalDateTime.now());
		}
		return deliveryRepository.save(delivery);
	}

	@Override
	@Transactional
	public Delivery update(Integer id, Delivery deliveryDetails) {
		Delivery existingDelivery = findById(id); // Kiểm tra tồn tại

		// 📝 Cập nhật các trường
		existingDelivery.setDeliveryName(deliveryDetails.getDeliveryName());
		existingDelivery.setDescription(deliveryDetails.getDescription());
		existingDelivery.setBasePrice(deliveryDetails.getBasePrice());
		existingDelivery.setPricePerKM(deliveryDetails.getPricePerKM());
		existingDelivery.setMaxDistance(deliveryDetails.getMaxDistance());
		existingDelivery.setIsActive(deliveryDetails.getIsActive());

		// @PreUpdate trong Entity sẽ tự động cập nhật updateAt

		return deliveryRepository.save(existingDelivery);
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		Delivery delivery = findById(id); 
		deliveryRepository.delete(delivery);
	}

	private DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
            delivery.getId(),
            delivery.getDeliveryName(),
            delivery.getDescription(),
            delivery.getBasePrice(),
            delivery.getPricePerKM(),
            delivery.getMaxDistance(),
            delivery.getIsActive(),
            delivery.getCreatedAt(),
            delivery.getUpdatedAt()
        );
    }
    
    // 💡 Hàm tiện ích để chuyển đổi từ Request DTO sang Entity
    private Delivery toEntity(DeliveryRequest request) {
        Delivery delivery = new Delivery();
        delivery.setDeliveryName(request.getDeliveryName());
        delivery.setDescription(request.getDescription());
        delivery.setBasePrice(request.getBasePrice());
        delivery.setPricePerKM(request.getPricePerKM() != null ? request.getPricePerKM() : BigDecimal.ZERO);
        delivery.setMaxDistance(request.getMaxDistance());
        delivery.setIsActive(request.getIsActive() != null ? request.getIsActive() : false);
        // createAt và updateAt sẽ được xử lý bởi @PrePersist/@PreUpdate
        return delivery;
    }

    @Override
    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        // 1. Chuyển đổi Request DTO sang Entity
        Delivery newDelivery = toEntity(request);
        
        // 2. Lưu vào cơ sở dữ liệu
        Delivery savedDelivery = deliveryRepository.save(newDelivery);
        
        // 3. Chuyển đổi Entity đã lưu sang Response DTO và trả về
        return toResponse(savedDelivery);
    }

    @Override
    public DeliveryResponse findDeliveryResponseById(Integer id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(); 
        return toResponse(delivery);
    }

	@Override
    public DeliveryResponse updateDelivery(Integer id, DeliveryRequest request) {
        Delivery existingDelivery = deliveryRepository.findById(id)
                .orElseThrow(); 

        // 📝 Ánh xạ từ Request DTO vào Entity hiện tại
        existingDelivery.setDeliveryName(request.getDeliveryName());
        existingDelivery.setDescription(request.getDescription());
        existingDelivery.setBasePrice(request.getBasePrice());
        existingDelivery.setPricePerKM(request.getPricePerKM() != null ? request.getPricePerKM() : BigDecimal.ZERO);
        existingDelivery.setMaxDistance(request.getMaxDistance());
        existingDelivery.setIsActive(request.getIsActive()); // Cập nhật trạng thái
        
        // @PreUpdate trong Entity sẽ tự động cập nhật updateAt

        Delivery updatedDelivery = deliveryRepository.save(existingDelivery);
        return toResponse(updatedDelivery);
    }

}
