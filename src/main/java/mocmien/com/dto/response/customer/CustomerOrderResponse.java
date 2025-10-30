package mocmien.com.dto.response.customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import mocmien.com.enums.OrderStatus;

public class CustomerOrderResponse {
	private String orderId;
	private LocalDateTime createdAt;
	private BigDecimal totalAmount;
	private OrderStatus status;
	private String storeName;

	// constructor
	public CustomerOrderResponse(String orderId, LocalDateTime createdAt, BigDecimal totalAmount, OrderStatus status,
			String storeName) {
		this.setOrderId(orderId);
		this.setCreatedAt(createdAt);
		this.setTotalAmount(totalAmount);
		this.setStatus(status);
		this.setStoreName(storeName);
	}

	// getter/setter...

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

}
