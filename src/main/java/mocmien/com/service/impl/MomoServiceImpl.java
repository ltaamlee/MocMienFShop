package mocmien.com.service.impl;

import mocmien.com.dto.response.customer.MomoCreateResponseDto;
import mocmien.com.dto.response.customer.MomoPaymentResponse;
import mocmien.com.entity.Orders;
import mocmien.com.entity.UserProfile;
import mocmien.com.enums.OrderStatus;
import mocmien.com.enums.PaymentStatus;
import mocmien.com.repository.OrdersRepository;
import mocmien.com.service.MomoService;
import mocmien.com.MomoSignatureUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MomoServiceImpl implements MomoService {

    @Value("${momo.partnerCode}")
    private String partnerCode;
    @Value("${momo.accessKey}")
    private String accessKey;
    @Value("${momo.secretKey}")
    private String secretKey;
    @Value("${momo.requestUrl}")
    private String requestUrl;
    @Value("${momo.notifyUrl}")
    private String notifyUrl;
    @Value("${momo.returnUrl}")
    private String returnUrl;

    private final OrdersRepository ordersRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public MomoServiceImpl(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    // ✅ 1. Tạo thanh toán QR nhanh (chưa lưu đơn hàng)
    @Override
    public MomoPaymentResponse createQuickPayment(BigDecimal amount, String orderInfo) {
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();

        String rawSignature = String.format(
                "accessKey=%s&amount=%d&extraData=&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=captureWallet",
                accessKey, amount.intValue(), notifyUrl, orderId, orderInfo, partnerCode, returnUrl, requestId);
        String signature = MomoSignatureUtil.hmacSHA256(rawSignature, secretKey);

        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("accessKey", accessKey);
        body.put("requestId", requestId);
        body.put("amount", amount.intValue());
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", returnUrl);
        body.put("ipnUrl", notifyUrl);
        body.put("extraData", "");
        body.put("requestType", "captureWallet");
        body.put("signature", signature);
        body.put("lang", "vi");

        ResponseEntity<Map> resp = restTemplate.postForEntity(requestUrl, body, Map.class);
        Map<?, ?> resBody = resp.getBody();

        MomoPaymentResponse res = new MomoPaymentResponse();
        if (resBody != null) {
            res.setPayUrl((String) resBody.get("payUrl"));
            res.setQrCodeUrl((String) resBody.get("qrCodeUrl"));
        }
        return res;
    }

    // ✅ 2. Tạo đơn hàng mới cho COD hoặc MoMo
    @Override
    public Orders createOrder(UserProfile profile, String receiverName, String phone, String address, String note) {
        Orders order = new Orders();
        order.setCustomer(profile);
        order.setStatus(OrderStatus.PENDING);
        order.setIsPaid(false);
        order.setPaymentMethod(null);
        order.setNote(note);
        order.setCreatedAt(LocalDateTime.now());
        // ⚠️ Entity Orders không có các field như receiverName, address, phone nên bỏ qua
        return ordersRepository.save(order);
    }

    // ✅ 3. Tạo thanh toán MoMo thật cho đơn hàng đã có
    @Override
    public MomoCreateResponseDto createPayment(Orders order) {
        String requestId = UUID.randomUUID().toString();
        // vì id của order là String (VD: "ORD-1730292789473"), không cần parse int
        String momoOrderId = order.getId() + "-" + System.currentTimeMillis();

        long amount = order.getAmountFromCustomer().longValue();
        String orderInfo = "Thanh toán đơn hàng " + order.getId();

        String rawSignature = String.format(
                "accessKey=%s&amount=%d&extraData=&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=captureWallet",
                accessKey, amount, notifyUrl, momoOrderId, orderInfo, partnerCode, returnUrl, requestId);
        String signature = MomoSignatureUtil.hmacSHA256(rawSignature, secretKey);

        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("accessKey", accessKey);
        body.put("requestId", requestId);
        body.put("amount", amount);
        body.put("orderId", momoOrderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", returnUrl);
        body.put("ipnUrl", notifyUrl);
        body.put("extraData", "");
        body.put("requestType", "captureWallet");
        body.put("signature", signature);
        body.put("lang", "vi");

        ResponseEntity<MomoCreateResponseDto> resp =
                restTemplate.postForEntity(requestUrl, body, MomoCreateResponseDto.class);

        System.out.println("📦 Gửi yêu cầu MoMo cho đơn " + order.getId());
        return resp.getBody();
    }

    // ✅ 4. Xử lý khi người dùng quay lại sau thanh toán MoMo
    @Override
    public boolean handleMomoReturn(String orderId, int resultCode) {
        if (resultCode == 0) return true; // thành công

        try {
            // orderId của Orders là String → không parse int nữa
            String realId = orderId.split("-")[0]; // VD: ORD-1730...
            Orders order = ordersRepository.findById(realId).orElse(null);
            if (order != null && !order.getIsPaid()) {
                ordersRepository.delete(order);
                System.out.println("🚮 Đã xóa đơn " + realId + " do người dùng hủy thanh toán.");
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ 5. Callback IPN từ MoMo (server-to-server)
    @Override
    public void handleCallback(String orderId, int resultCode) {
        try {
            String realId = orderId.split("-")[0]; // lấy phần ORD-xxxxxx
            Orders order = ordersRepository.findById(realId).orElse(null);
            if (order == null) return;

            if (resultCode == 0) {
                order.setIsPaid(true);
                order.setStatus(OrderStatus.CONFIRMED); // thanh toán thành công
            } else {
                order.setIsPaid(false);
                order.setStatus(OrderStatus.CANCELED); // thất bại
            }

            ordersRepository.save(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
