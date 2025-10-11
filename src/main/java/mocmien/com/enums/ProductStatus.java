package mocmien.com.enums;

public enum ProductStatus {
    DANG_BAN(1, "Đang bán"),
    NGUNG_BAN(0, "Ngừng bán"),
    HET_HANG(-1, "Hết hàng");

    private final int value;
    private final String displayName;

    ProductStatus(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Chuyển từ int sang enum
    public static ProductStatus fromValue(int value) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid product status value: " + value);
    }

    // Kiểm tra có thể bán không
    public boolean canSell() {
        return this == DANG_BAN;
    }

    // Kiểm tra có hiển thị trên web không
    public boolean isVisible() {
        return this == DANG_BAN || this == HET_HANG;
    }

    // Kiểm tra có đang hoạt động không
    public boolean isActive() {
        return this == DANG_BAN;
    }
}