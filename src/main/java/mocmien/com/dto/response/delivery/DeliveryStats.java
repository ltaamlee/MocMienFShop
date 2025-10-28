package mocmien.com.dto.response.delivery;

public record DeliveryStats(
		long totalDeliveries,
        long activeDeliveries,
        long inactiveDeliveries		
		) {

}
