package mocmien.com.enums;

public enum StaffPosition {
    SALE("Nhân viên bán hàng"),
    INVENTORY("Nhân viên kho"),
    MANAGER("Quản lý cửa hàng"),
    CASHIER("Thu ngân"),
    CUSTOMER_SUPPORT("Hỗ trợ khách hàng"),
    DELIVERY("Nhân viên giao hàng"),
    MARKETING("Nhân viên marketing"),
    ACCOUNTANT("Kế toán");

    private final String displayName;

    StaffPosition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
