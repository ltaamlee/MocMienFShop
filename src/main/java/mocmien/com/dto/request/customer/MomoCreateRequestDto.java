package mocmien.com.dto.request.customer;

public class MomoCreateRequestDto {
    private String partnerCode;
    private String accessKey;
    private String requestId;
    private String amount;
    private String orderId;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType;
    private String extraData;
    private String signature;
    private String lang = "vi";

    public MomoCreateRequestDto() {}

    // ✅ Constructor KHỚP với cách bạn đang gọi trong service
    public MomoCreateRequestDto(String partnerCode, String accessKey, String requestId,
                                String amount, String orderId, String orderInfo,
                                String redirectUrl, String ipnUrl, String requestType,
                                String extraData, String signature) {
        this.partnerCode = partnerCode;
        this.accessKey = accessKey;
        this.requestId = requestId;
        this.amount = amount;
        this.orderId = orderId;
        this.orderInfo = orderInfo;
        this.redirectUrl = redirectUrl;
        this.ipnUrl = ipnUrl;
        this.requestType = requestType;
        this.extraData = extraData;
        this.signature = signature;
        this.lang = "vi";
    }

    // ✅ Getter/Setter đầy đủ
    public String getPartnerCode() { return partnerCode; }
    public void setPartnerCode(String partnerCode) { this.partnerCode = partnerCode; }

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderInfo() { return orderInfo; }
    public void setOrderInfo(String orderInfo) { this.orderInfo = orderInfo; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    public String getIpnUrl() { return ipnUrl; }
    public void setIpnUrl(String ipnUrl) { this.ipnUrl = ipnUrl; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }
}
