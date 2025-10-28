package mocmien.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mocmien.com.entity.Delivery;
import mocmien.com.repository.DeliveryRepository;
import mocmien.com.service.DeliveryService;

@Service
public class DeliveryServiceImpl implements DeliveryService{

	@Autowired
	private DeliveryRepository deliveryRepository;
	
	@Override
	public List<Delivery> getAllActiveDeliveries() {
		return deliveryRepository.findByIsActiveTrue();
	}

}
