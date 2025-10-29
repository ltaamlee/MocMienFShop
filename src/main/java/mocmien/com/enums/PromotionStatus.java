package mocmien.com.enums;

public enum PromotionStatus {
    INACTIVE("Chưa kích hoạt"),
    ACTIVE("Đang hoạt động"),
    EXPIRED("Đã hết hạn"),
    SCHEDULED("Hẹn giờ");

    private final String displayName;

	PromotionStatus(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
