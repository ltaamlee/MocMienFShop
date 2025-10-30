package mocmien.com.dto.response.revenue;

import java.math.BigDecimal;

public class RevenueSummaryResponse {
	private BigDecimal gross; // tổng tiền khách trả (DELIVERED)
	private long orders; // số đơn đã giao
	private BigDecimal avgOrder; // gross / orders
	private BigDecimal platformFee; // amountToSys
	private BigDecimal netToStore; // amountToStore

	public RevenueSummaryResponse() {
	}

	public RevenueSummaryResponse(BigDecimal gross, long orders, BigDecimal avgOrder, BigDecimal platformFee,
			BigDecimal netToStore) {
		this.gross = gross;
		this.orders = orders;
		this.avgOrder = avgOrder;
		this.platformFee = platformFee;
		this.netToStore = netToStore;
	}

	// getters/setters
	public BigDecimal getGross() {
		return gross;
	}

	public void setGross(BigDecimal gross) {
		this.gross = gross;
	}

	public long getOrders() {
		return orders;
	}

	public void setOrders(long orders) {
		this.orders = orders;
	}

	public BigDecimal getAvgOrder() {
		return avgOrder;
	}

	public void setAvgOrder(BigDecimal avgOrder) {
		this.avgOrder = avgOrder;
	}

	public BigDecimal getPlatformFee() {
		return platformFee;
	}

	public void setPlatformFee(BigDecimal platformFee) {
		this.platformFee = platformFee;
	}

	public BigDecimal getNetToStore() {
		return netToStore;
	}

	public void setNetToStore(BigDecimal netToStore) {
		this.netToStore = netToStore;
	}
}
