package mocmien.com.dto.response.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorPromotionStatsResponse {
	private long totalPromotions;
	private long inactivePromotions;
	private long activePromotions;
	private long expiringSoonPromotions;
	private long expiredPromotions;
	public long getTotalPromotions() {
		return totalPromotions;
	}
	public void setTotalPromotions(long totalPromotions) {
		this.totalPromotions = totalPromotions;
	}
	public long getInactivePromotions() {
		return inactivePromotions;
	}
	public void setInactivePromotions(long inactivePromotions) {
		this.inactivePromotions = inactivePromotions;
	}
	public long getActivePromotions() {
		return activePromotions;
	}
	public void setActivePromotions(long activePromotions) {
		this.activePromotions = activePromotions;
	}
	public long getExpiringSoonPromotions() {
		return expiringSoonPromotions;
	}
	public void setExpiringSoonPromotions(long expiringSoonPromotions) {
		this.expiringSoonPromotions = expiringSoonPromotions;
	}
	public long getExpiredPromotions() {
		return expiredPromotions;
	}
	public void setExpiredPromotions(long expiredPromotions) {
		this.expiredPromotions = expiredPromotions;
	}
	
	
}
