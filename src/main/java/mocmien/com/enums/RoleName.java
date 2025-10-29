package mocmien.com.enums;

public enum RoleName {
    ADMIN("Quản trị viên"),
    CUSTOMER("Khách hàng"),
    VENDOR("Chủ cửa hàng"),
    STAFF("Nhân viên cửa hàng"),
    SHIPPER("Nhân viên giao hàng");

    private final String displayName;

    RoleName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
