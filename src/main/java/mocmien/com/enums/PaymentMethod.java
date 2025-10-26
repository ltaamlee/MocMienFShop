package mocmien.com.enums;

public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    VNPAY("Thanh toán qua VNPAY"),
    MOMO("Thanh toán qua MoMo"),
    ZALOPAY("Thanh toán qua ZaloPay"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    PAYPAL("Thanh toán qua PayPal"),
    OTHER("Phương thức khác");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
