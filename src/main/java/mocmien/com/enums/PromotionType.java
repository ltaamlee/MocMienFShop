package mocmien.com.enums;

public enum PromotionType {
    PERCENT("Giảm theo %"),
    AMOUNT("Giảm tiền"),   
    FREESHIP("Miễn phí vận chuyển");

	private final String displayName;

	PromotionType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
