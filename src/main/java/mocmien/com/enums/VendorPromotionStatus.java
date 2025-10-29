package mocmien.com.enums;

public enum VendorPromotionStatus {
	INACTIVE("Chưa kích hoạt"), ACTIVE("Đang hoạt động"), EXPIRED("Đã hết hạn");

	private final String displayName;

	VendorPromotionStatus(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}