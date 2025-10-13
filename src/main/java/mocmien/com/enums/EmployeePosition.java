package mocmien.com.enums;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
public enum EmployeePosition {
	ADMIN("Quản trị viên"),
    MANAGER("Quản lý"),
    SALES_EMPLOYEE("Nhân viên bán hàng"),
    INVENTORY_EMPLOYEE("Nhân viên kho"),
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
    public boolean isManager() {
        return this == MANAGER;
    }

    public boolean isSalesRelated() {
        return this == SALES_EMPLOYEE || this == MANAGER;
    }

    public boolean isInventoryRelated() {
        return this == INVENTORY_EMPLOYEE;
    }
}
