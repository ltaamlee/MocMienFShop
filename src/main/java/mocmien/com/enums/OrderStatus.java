package mocmien.com.enums;

public enum OrderStatus {
    
    NEW("Đơn hàng mới"),
    PENDING("Chờ xử lý"),
    CONFIRMED("Đã xác nhận"),
    SHIPPING("Đang giao"),
    DELIVERED("Đã giao"),
    CANCELED("Hủy"),
    RETURNED_REFUNDED("Trả hàng - Hoàn tiền");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
