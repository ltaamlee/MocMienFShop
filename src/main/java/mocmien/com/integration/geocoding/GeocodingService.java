package mocmien.com.integration.geocoding;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeocodingService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeocodingService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Lấy tọa độ (latitude, longitude) từ địa chỉ sử dụng Nominatim OpenStreetMap
     * 
     * @param address Địa chỉ đầy đủ (VD: "123 Nguyễn Huệ, Quận 1, TP.HCM, Việt Nam")
     * @return Map chứa "latitude" và "longitude", hoặc null nếu không tìm thấy
     */
    public Map<String, Double> geocodeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }

        try {
            // Build URL với tham số
            String url = String.format("%s?q=%s&format=json&limit=1&countrycodes=vn",
                    NOMINATIM_URL,
                    java.net.URLEncoder.encode(address, "UTF-8"));

            System.out.println("🌍 Geocoding address: " + address);
            System.out.println("   URL: " + url);

            // Call API
            String response = restTemplate.getForObject(url, String.class);

            // Parse JSON
            JsonNode root = objectMapper.readTree(response);
            if (root != null && root.isArray() && root.size() > 0) {
                JsonNode first = root.get(0);
                double lat = first.get("lat").asDouble();
                double lon = first.get("lon").asDouble();

                System.out.println("   ✅ Success: lat=" + lat + ", lon=" + lon);
                return Map.of("latitude", lat, "longitude", lon);
            }

            System.err.println("   ❌ No results found for address: " + address);
            return null;
        } catch (Exception e) {
            System.err.println("   ❌ Geocoding error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tạo địa chỉ đầy đủ từ các thành phần
     */
    public String buildFullAddress(String street, String ward, String district, String city) {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.trim().isEmpty()) {
            sb.append(street.trim());
        }
        if (ward != null && !ward.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ward.trim());
        }
        if (district != null && !district.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district.trim());
        }
        if (city != null && !city.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city.trim());
        }
        if (sb.length() > 0) {
            sb.append(", Việt Nam");
        }
        return sb.toString();
    }
}

