package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mocmien.com.entity.CustomerAddress;
import mocmien.com.entity.Store;
import mocmien.com.integration.ghn.GhnClient;
import mocmien.com.integration.geocoding.GeocodingService;
import mocmien.com.service.ShippingService;

@Service
public class ShippingServiceImpl implements ShippingService {

	@Autowired
	private GhnClient ghnClient;

	@Autowired
	private GeocodingService geocodingService;

	@Value("${shipping.free_radius_km:3}")
	private BigDecimal freeRadiusKm;

	@Value("${shipping.max_radius_km:100}")
	private BigDecimal maxRadiusKm;

	@Value("${shipping.fallback_fee:30000}")
	private BigDecimal fallbackFee;

	@Override
	public BigDecimal calculateShippingFee(Store store, CustomerAddress toAddress, int totalWeightGram) {
		BigDecimal distance = calculateDistanceKm(store, toAddress);
		
		// Nếu không tính được khoảng cách, dùng phí mặc định
		if (distance == null) {
			System.err.println("⚠️ WARNING: Không tính được khoảng cách. Dùng phí mặc định.");
			System.err.println("   - Shop: " + (store != null ? store.getStoreName() : "null"));
			System.err.println("   - Shop address: " + (store != null ? store.getAddress() : "null"));
			System.err.println("   - Customer address: " + (toAddress != null ? 
				toAddress.getLine() + ", " + toAddress.getWard() + ", " + toAddress.getDistrict() : "null"));
			
			// Dùng phí fallback thay vì throw exception
			return fallbackFee;
		}

		// Miễn phí ship trong bán kính freeRadiusKm (mặc định 3km)
		if (distance.compareTo(freeRadiusKm) <= 0) {
			return BigDecimal.ZERO;
		}

		// Không giao hàng quá xa (mặc định 100km)
		if (distance.compareTo(maxRadiusKm) > 0) {
			throw new IllegalArgumentException("Đơn hàng không áp dụng tại vị trí của bạn (vượt quá " + maxRadiusKm + "km)");
		}

		// Tính phí ship dựa trên khoảng cách và trọng lượng
		// Công thức: phí cơ bản + (khoảng cách * 3000) + (trọng lượng/1000 * 2000)
		BigDecimal baseFee = BigDecimal.valueOf(15000); // Phí cơ bản 15k
		BigDecimal distanceFee = distance.multiply(BigDecimal.valueOf(3000)); // 3k/km
		BigDecimal weightFee = BigDecimal.valueOf(totalWeightGram)
				.divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(2000)); // 2k/kg

		BigDecimal totalFee = baseFee.add(distanceFee).add(weightFee);

		// Làm tròn lên nghìn
		return totalFee.divide(BigDecimal.valueOf(1000), 0, RoundingMode.UP)
				.multiply(BigDecimal.valueOf(1000));
	}

	@Override
	public BigDecimal calculateDistanceKm(Store store, CustomerAddress toAddress) {
		if (store == null || toAddress == null) return null;

		// Lấy tọa độ shop (tự động geocode nếu chưa có)
		Double storeLat = store.getLatitude() != null ? store.getLatitude().doubleValue() : null;
		Double storeLon = store.getLongitude() != null ? store.getLongitude().doubleValue() : null;

		if (storeLat == null || storeLon == null) {
			// Thử geocode địa chỉ shop (Store chỉ có 1 field address duy nhất)
			String storeAddress = store.getAddress();
			if (storeAddress != null && !storeAddress.trim().isEmpty()) {
				// Thêm ", Việt Nam" để tăng độ chính xác
				storeAddress = storeAddress + ", Việt Nam";
			}
			Map<String, Double> storeCoords = geocodingService.geocodeAddress(storeAddress);
			if (storeCoords != null) {
				storeLat = storeCoords.get("latitude");
				storeLon = storeCoords.get("longitude");
				// Có thể lưu lại vào DB để lần sau không phải geocode nữa
				// store.setLatitude(BigDecimal.valueOf(storeLat));
				// store.setLongitude(BigDecimal.valueOf(storeLon));
			} else {
				return null; // Không tìm được tọa độ shop
			}
		}

		// Lấy tọa độ địa chỉ khách hàng (tự động geocode nếu chưa có)
		Double customerLat = toAddress.getLatitude() != null ? toAddress.getLatitude().doubleValue() : null;
		Double customerLon = toAddress.getLongitude() != null ? toAddress.getLongitude().doubleValue() : null;

		if (customerLat == null || customerLon == null) {
			// Thử geocode địa chỉ khách hàng (line, ward, district, province)
			String customerAddress = geocodingService.buildFullAddress(
				toAddress.getLine(),       // số nhà
				toAddress.getWard(),       // phường/xã
				toAddress.getDistrict(),   // huyện
				toAddress.getProvince()    // tỉnh/thành phố
			);
			Map<String, Double> customerCoords = geocodingService.geocodeAddress(customerAddress);
			if (customerCoords != null) {
				customerLat = customerCoords.get("latitude");
				customerLon = customerCoords.get("longitude");
				// Có thể lưu lại vào DB
				// toAddress.setLatitude(BigDecimal.valueOf(customerLat));
				// toAddress.setLongitude(BigDecimal.valueOf(customerLon));
			} else {
				return null; // Không tìm được tọa độ khách hàng
			}
		}

		// Tính khoảng cách bằng công thức Haversine
		return BigDecimal.valueOf(haversine(storeLat, storeLon, customerLat, customerLon))
				.setScale(2, RoundingMode.HALF_UP);
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
