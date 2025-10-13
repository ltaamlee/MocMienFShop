package mocmien.com.enums;

public enum OrderStatus {
    CHO_XU_LY("Chờ xử lý"),
    DA_XAC_NHAN("Đã xác nhận"),
    DANG_GIAO("Đang giao hàng"),
    HOAN_TAT("Hoàn tất"),
    HUY("Đã hủy"),
    TRA_HANG("Trả hàng");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Kiểm tra có thể hủy đơn không
    public boolean canCancel() {
        return this == CHO_XU_LY || this == DA_XAC_NHAN;
    }

    // Kiểm tra đơn đã hoàn thành chưa
    public boolean isCompleted() {
        return this == HOAN_TAT || this == HUY || this == TRA_HANG;
    }

    // Kiểm tra có thể chỉnh sửa không
    public boolean canEdit() {
        return this == CHO_XU_LY;
    }

    // Kiểm tra có đang vận chuyển không
    public boolean isInTransit() {
        return this == DANG_GIAO;
    }

    // Kiểm tra có thể xác nhận không
    public boolean canConfirm() {
        return this == CHO_XU_LY;
    }

    // Kiểm tra có thể giao hàng không
    public boolean canShip() {
        return this == DA_XAC_NHAN;
    }

    // Kiểm tra có thể hoàn tất không
    public boolean canComplete() {
        return this == DANG_GIAO;
    }

    // Lấy trạng thái tiếp theo trong flow
    public OrderStatus getNextStatus() {
        switch (this) {
            case CHO_XU_LY:
                return DA_XAC_NHAN;
            case DA_XAC_NHAN:
                return DANG_GIAO;
            case DANG_GIAO:
                return HOAN_TAT;
            default:
                return this; // Không có trạng thái tiếp theo
        }
    }
}