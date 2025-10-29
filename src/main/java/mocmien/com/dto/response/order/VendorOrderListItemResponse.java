package mocmien.com.dto.response.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import mocmien.com.enums.OrderStatus;

@Data
public class VendorOrderListItemResponse {
    private String id;
    private String customerName;
    private LocalDateTime createdAt;
    private BigDecimal total;               // amountFromCustomer
    private String paymentMethodDisplay;    // VNPAY/MOMO/COD...
    private OrderStatus status;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public String getPaymentMethodDisplay() {
		return paymentMethodDisplay;
	}
	public void setPaymentMethodDisplay(String paymentMethodDisplay) {
		this.paymentMethodDisplay = paymentMethodDisplay;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
    
}
