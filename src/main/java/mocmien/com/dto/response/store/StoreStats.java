package mocmien.com.dto.response.store;

public record StoreStats(
		
		long totalStores,
	    long activeStores,
	    long inactiveStores,
	    long blockedStores) {

}
