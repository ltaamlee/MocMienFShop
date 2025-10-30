package mocmien.com.enums;

public enum PaymentStatus {
    CHUA_THANH_TOAN("Chưa thanh toán"),
    DA_THANH_TOAN("Đã thanh toán"),
    HOAN_TIEN("Đã hoàn tiền"),
	THAT_BAI("Thất bại");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Kiểm tra đã thanh toán chưa
    public boolean isPaid() {
        return this == DA_THANH_TOAN;
    }

    // Kiểm tra có thể hoàn tiền không
    public boolean canRefund() {
        return this == DA_THANH_TOAN;
    }
}
