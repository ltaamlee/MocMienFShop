package mocmien.com.integration.ghn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class GhnClient {

	@Value("${ghn.token:}")
	private String token;

	@Value("${ghn.shop_id:0}")
	private int shopId;

	@Value("${ghn.base_url:https://online-gateway.ghn.vn/shiip/public-api}")
	private String baseUrl;

	private final RestTemplate restTemplate = new RestTemplate();

	public Long calculateFee(Integer fromDistrictId,
	                         Integer toDistrictId,
	                         String toWardCode,
	                         int weightGram,
	                         int serviceTypeId) {
		try {
			String url = baseUrl + "/v2/shipping-order/fee";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Token", token);
			headers.add("ShopId", String.valueOf(shopId));

			Map<String, Object> body = new HashMap<>();
			body.put("service_type_id", serviceTypeId);
			body.put("from_district_id", fromDistrictId);
			body.put("to_district_id", toDistrictId);
			body.put("to_ward_code", toWardCode);
			body.put("weight", weightGram);

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
			ResponseEntity<Map> raw = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
			Map<String, Object> respBody = raw.getBody();
			if (raw.getStatusCode().is2xxSuccessful() && respBody != null) {
				Object data = respBody.get("data");
				if (data instanceof Map) {
					Object total = ((Map<?, ?>) data).get("total");
					if (total instanceof Number) return ((Number) total).longValue();
				}
			}
		} catch (Exception ignore) { }
		return null;
	}
}
