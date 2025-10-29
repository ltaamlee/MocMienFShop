package mocmien.com.dto.response.customer;

import lombok.Data;

@Data
public class MomoPaymentResponse {
    private String payUrl;
    private String qrCodeUrl;
	public String getPayUrl() {
		return payUrl;
	}
	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}
	public String getQrCodeUrl() {
		return qrCodeUrl;
	}
	public void setQrCodeUrl(String qrCodeUrl) {
		this.qrCodeUrl = qrCodeUrl;
	}

}
