package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mocmien.com.entity.CustomerAddress;
import mocmien.com.entity.Delivery;
import mocmien.com.entity.Store;
import mocmien.com.integration.geocoding.GeocodingService;
import mocmien.com.repository.DeliveryRepository;
import mocmien.com.service.ShippingService;
import mocmien.com.util.DistanceUtil;

import java.util.Optional;

@Service
public class ShippingServiceImpl implements ShippingService {

	@Autowired
	private GeocodingService geocodingService;

	@Autowired
	private DeliveryRepository deliveryRepository;

	@Override
	public BigDecimal calculateShippingFee(Store store, CustomerAddress toAddress, int totalWeightGram) {
		// Bước 1: Tính khoảng cách
		BigDecimal distance = calculateDistanceKm(store, toAddress);
		
		// Nếu không tính được khoảng cách, báo lỗi
		if (distance == null) {
			System.err.println("⚠️ WARNING: Không tính được khoảng cách.");
			System.err.println("   - Shop: " + (store != null ? store.getStoreName() : "null"));
			System.err.println("   - Shop address: " + (store != null ? store.getAddress() : "null"));
			System.err.println("   - Customer address: " + (toAddress != null ? 
				toAddress.getLine() + ", " + toAddress.getWard() + ", " + toAddress.getDistrict() : "null"));
			
			throw new IllegalArgumentException("Không xác định được khoảng cách. Vui lòng kiểm tra lại địa chỉ.");
		}

		System.out.println("📍 Khoảng cách: " + distance + "km");

		// Bước 2: Tìm nhà vận chuyển phù hợp với khoảng cách
		Integer distanceKm = distance.intValue();
		Optional<Delivery> deliveryOpt = deliveryRepository.findFirstAvailableForDistance(distanceKm);
		
		if (deliveryOpt.isEmpty()) {
			System.err.println("❌ Không có nhà vận chuyển nào hỗ trợ khoảng cách " + distanceKm + "km");
			throw new IllegalArgumentException(
				"Đơn hàng không áp dụng tại địa chỉ của bạn (khoảng cách " + distanceKm + 
				"km vượt quá giới hạn của tất cả nhà vận chuyển)"
			);
		}

		Delivery delivery = deliveryOpt.get();
		System.out.println("🚚 Nhà vận chuyển: " + delivery.getDeliveryName());
		System.out.println("   - Phí cơ bản: " + delivery.getBasePrice() + "đ");
		System.out.println("   - Giá/km: " + delivery.getPricePerKM() + "đ");
		System.out.println("   - Max distance: " + delivery.getMaxDistance() + "km");

		// Bước 3: Tính phí ship
		// Công thức: basePrice + (distance * pricePerKM)
		BigDecimal baseFee = delivery.getBasePrice() != null ? delivery.getBasePrice() : BigDecimal.ZERO;
		BigDecimal pricePerKM = delivery.getPricePerKM() != null ? delivery.getPricePerKM() : BigDecimal.ZERO;
		BigDecimal distanceFee = distance.multiply(pricePerKM);

		BigDecimal totalFee = baseFee.add(distanceFee);

		// Làm tròn lên nghìn
		BigDecimal roundedFee = totalFee.divide(BigDecimal.valueOf(1000), 0, RoundingMode.UP)
				.multiply(BigDecimal.valueOf(1000));

		System.out.println("💰 Phí ship: " + roundedFee + "đ (= " + baseFee + " + " + distance + " × " + pricePerKM + ")");

		return roundedFee;
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

		// Tính khoảng cách bằng DistanceUtil (Haversine formula)
		double distanceKm = DistanceUtil.calculateDistance(storeLat, storeLon, customerLat, customerLon);
		return BigDecimal.valueOf(distanceKm).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public Delivery findDeliveryForDistance(Store store, CustomerAddress toAddress) {
		// Tính khoảng cách
		BigDecimal distance = calculateDistanceKm(store, toAddress);
		
		if (distance == null) {
			return null;
		}

		// Tìm delivery phù hợp
		Integer distanceKm = distance.intValue();
		return deliveryRepository.findFirstAvailableForDistance(distanceKm).orElse(null);
	}
}
