package mocmien.com.dto.response.revenue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VendorRevenueTableItem {
	private String orderId;
	private String customerName;
	private LocalDateTime createdAt;
	private BigDecimal total;
	private String paymentMethod;

	public VendorRevenueTableItem() {
	}

	public VendorRevenueTableItem(String orderId, String customerName, LocalDateTime createdAt, BigDecimal total,
			String paymentMethod) {
		this.orderId = orderId;
		this.customerName = customerName;
		this.createdAt = createdAt;
		this.total = total;
		this.paymentMethod = paymentMethod;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
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

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}
