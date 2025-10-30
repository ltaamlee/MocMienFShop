package mocmien.com.service;

import java.math.BigDecimal;

import mocmien.com.entity.CustomerAddress;
import mocmien.com.entity.Delivery;
import mocmien.com.entity.Store;

public interface ShippingService {

	BigDecimal calculateShippingFee(Store store, CustomerAddress toAddress, int totalWeightGram);

	BigDecimal calculateDistanceKm(Store store, CustomerAddress toAddress);

	/**
	 * Tìm nhà vận chuyển phù hợp với khoảng cách
	 */
	Delivery findDeliveryForDistance(Store store, CustomerAddress toAddress);
}
