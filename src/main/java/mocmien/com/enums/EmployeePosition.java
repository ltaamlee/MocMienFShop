package mocmien.com.enums;

public enum EmployeePosition {
	ADMIN("Quản trị viên"),
    SELLER("Quản lý cửa hàng"),
    SHIPPER("Nhân viên giao hàng");

    private final String displayName;

    EmployeePosition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
    public boolean isSeller() {
        return this == SELLER;
    }


    public boolean isShipper() {
        return this == SHIPPER;
    }
}