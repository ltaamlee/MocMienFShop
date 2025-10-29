package mocmien.com.service;

import java.math.BigDecimal;

import mocmien.com.dto.response.customer.MomoCreateResponseDto;
import mocmien.com.dto.response.customer.MomoPaymentResponse;
import mocmien.com.entity.Orders;
import mocmien.com.entity.UserProfile;

public interface MomoService {

    /**
     * Tạo yêu cầu thanh toán MoMo cho một đơn hàng đã có trong hệ thống.
     * @param order Đơn hàng cần thanh toán
     * @return MomoCreateResponseDto chứa payUrl, qrCodeUrl, message, v.v.
     */
    MomoCreateResponseDto createPayment(Orders order);

    /**
     * Xử lý callback (IPN) từ MoMo gửi về server.
     * @param orderId Mã đơn hàng do MoMo trả về
     * @param resultCode Mã trạng thái thanh toán
     */
    void handleCallback(String orderId, int resultCode);

    /**
     * Tạo một giao dịch thanh toán nhanh qua MoMo (hiển thị QR mà chưa cần lưu đơn hàng).
     * @param amount Số tiền thanh toán
     * @param orderInfo Thông tin đơn hàng hiển thị trên MoMo
     * @return MomoPaymentResponse chứa URL thanh toán và QR code
     */
    MomoPaymentResponse createQuickPayment(BigDecimal amount, String orderInfo);

    /**
     * Xử lý khi người dùng quay lại trang web sau khi thanh toán MoMo (redirect URL).
     * @param orderId Mã đơn hàng MoMo trả về
     * @param resultCode Kết quả thanh toán (0 = thành công)
     * @return true nếu thanh toán thành công, false nếu thất bại hoặc bị hủy
     */
    boolean handleMomoReturn(String orderId, int resultCode);

    /**
     * Tạo mới một đơn hàng cơ bản trong hệ thống để chuẩn bị thanh toán qua MoMo hoặc COD.
     * @param profile Hồ sơ người dùng (UserProfile)
     * @param receiverName Tên người nhận hàng
     * @param phone Số điện thoại người nhận
     * @param address Địa chỉ giao hàng
     * @param note Ghi chú đơn hàng
     * @return Đối tượng Orders đã được lưu
     */
    Orders createOrder(UserProfile profile, String receiverName, String phone, String address, String note);
}
