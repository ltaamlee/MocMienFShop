package mocmien.com.dto.response.admin;

public record AdminPromotionStats(
		
		long total,
		long active,
		long upcoming,
		long expiring,
		long expired
		
		) {

}
