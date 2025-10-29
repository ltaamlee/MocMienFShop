package mocmien.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mocmien.com.dto.response.delivery.DeliveryStats;
import mocmien.com.entity.Delivery;
import mocmien.com.repository.DeliveryRepository;
import mocmien.com.service.DeliveryService;

@Service
public class DeliveryServiceImpl implements DeliveryService {

	@Autowired
	private DeliveryRepository deliveryRepository;

	@Override
	public List<Delivery> getAllActiveDeliveries() {
		return deliveryRepository.findByIsActiveTrue();
	}

	@Override
	public DeliveryStats getDeliveryStatistics() {
		long total = deliveryRepository.count(); // Tổng số đơn
		long active = deliveryRepository.countByIsActive(true); // Số đơn active
		long inactive = deliveryRepository.countByIsActive(false); // Số đơn inactive
		return new DeliveryStats(total, active, inactive);

	}

}
