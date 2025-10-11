package mocmien.com.enums;

public enum PaymentMethod {
    TIEN_MAT("Tiền mặt"),
    MOMO("Ví MoMo"),
    VNPAY("VNPay"),
    COD("Thanh toán khi nhận hàng");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Kiểm tra có cần thanh toán trước không
    public boolean requiresPrePayment() {
        return this == MOMO || this == VNPAY;
    }

    // Kiểm tra có phải thanh toán khi nhận hàng không
    public boolean isCashOnDelivery() {
        return this == COD || this == TIEN_MAT;
    }

    // Kiểm tra có phải thanh toán online không
    public boolean isOnlinePayment() {
        return this == MOMO || this == VNPAY;
    }
}