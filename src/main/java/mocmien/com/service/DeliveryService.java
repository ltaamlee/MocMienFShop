package mocmien.com.service;

import java.util.List;

import mocmien.com.dto.response.delivery.DeliveryStats;
import mocmien.com.entity.Delivery;

public interface DeliveryService{

	List<Delivery> getAllActiveDeliveries();

	DeliveryStats getDeliveryStatistics();

}
