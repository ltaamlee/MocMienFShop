package mocmien.com.dto.response.revenue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import mocmien.com.enums.OrderStatus;

public class RecentOrderItem {
	private String id;
	private LocalDateTime createdAt;
	private BigDecimal total;
	private OrderStatus status;
	private Boolean paid;

	public RecentOrderItem() {
	}

	public RecentOrderItem(String id, LocalDateTime createdAt, BigDecimal total, OrderStatus status, Boolean paid) {
		this.id = id;
		this.createdAt = createdAt;
		this.total = total;
		this.status = status;
		this.paid = paid;
	}

	// getters/setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}
}
