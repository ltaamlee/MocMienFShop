package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mocmien.com.entity.CustomerAddress;
import mocmien.com.entity.Store;
import mocmien.com.integration.ghn.GhnClient;
import mocmien.com.service.ShippingService;

@Service
public class ShippingServiceImpl implements ShippingService {

	@Autowired
	private GhnClient ghnClient;

	@Value("${shipping.free_radius_km:3}")
	private BigDecimal freeRadiusKm;

	@Value("${shipping.max_radius_km:100}")
	private BigDecimal maxRadiusKm;

	@Value("${shipping.fallback_fee:30000}")
	private BigDecimal fallbackFee;

	@Override
	public BigDecimal calculateShippingFee(Store store, CustomerAddress toAddress, int totalWeightGram) {
		BigDecimal distance = calculateDistanceKm(store, toAddress);
		if (distance == null) {
			throw new IllegalArgumentException("Không xác định được khoảng cách. Vui lòng cập nhật vị trí Shop/địa chỉ nhận hàng (tọa độ).");
		}

		if (distance.compareTo(freeRadiusKm) < 0) {
			return BigDecimal.ZERO;
		}
		if (distance.compareTo(maxRadiusKm) > 0) {
			throw new IllegalArgumentException("Đơn hàng không áp dụng tại vị trí của bạn (vượt quá " + maxRadiusKm + "km)");
		}

		Long fee = ghnClient.calculateFee(null, null, null, Math.max(totalWeightGram, 500), 2);
		if (fee == null) {
			throw new IllegalStateException("Không tính được phí vận chuyển từ Giao Hàng Nhanh. Vui lòng thử lại sau.");
		}
		return BigDecimal.valueOf(fee);
	}

	@Override
	public BigDecimal calculateDistanceKm(Store store, CustomerAddress toAddress) {
		if (store == null || toAddress == null) return null;
		if (store.getLatitude() == null || store.getLongitude() == null) return null;
		if (toAddress.getLatitude() == null || toAddress.getLongitude() == null) return null;

		double lat1 = store.getLatitude().doubleValue();
		double lon1 = store.getLongitude().doubleValue();
		double lat2 = toAddress.getLatitude().doubleValue();
		double lon2 = toAddress.getLongitude().doubleValue();

		return BigDecimal.valueOf(haversine(lat1, lon1, lat2, lon2)).setScale(2, RoundingMode.HALF_UP);
	}

	private double haversine(double lat1, double lon1, double lat2, double lon2) {
		final int R = 6371; // Earth radius in KM
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c;
	}
}
