package mocmien.com.dto.response.revenue;

import java.math.BigDecimal;

public class VendorRevenueSummaryResponse {
	private BigDecimal grossSales;
	private BigDecimal systemFee;
	private BigDecimal netToStore;
	private long deliveredOrders;
	private BigDecimal avgOrderValue;

	public VendorRevenueSummaryResponse() {
	}

	public VendorRevenueSummaryResponse(BigDecimal grossSales, BigDecimal systemFee, BigDecimal netToStore,
			long deliveredOrders, BigDecimal avgOrderValue) {
		this.grossSales = grossSales;
		this.systemFee = systemFee;
		this.netToStore = netToStore;
		this.deliveredOrders = deliveredOrders;
		this.avgOrderValue = avgOrderValue;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private BigDecimal grossSales;
		private BigDecimal systemFee;
		private BigDecimal netToStore;
		private long deliveredOrders;
		private BigDecimal avgOrderValue;

		public Builder grossSales(BigDecimal v) {
			this.grossSales = v;
			return this;
		}

		public Builder systemFee(BigDecimal v) {
			this.systemFee = v;
			return this;
		}

		public Builder netToStore(BigDecimal v) {
			this.netToStore = v;
			return this;
		}

		public Builder deliveredOrders(long v) {
			this.deliveredOrders = v;
			return this;
		}

		public Builder avgOrderValue(BigDecimal v) {
			this.avgOrderValue = v;
			return this;
		}

		public VendorRevenueSummaryResponse build() {
			return new VendorRevenueSummaryResponse(grossSales, systemFee, netToStore, deliveredOrders, avgOrderValue);
		}
	}

	public BigDecimal getGrossSales() {
		return grossSales;
	}

	public void setGrossSales(BigDecimal grossSales) {
		this.grossSales = grossSales;
	}

	public BigDecimal getSystemFee() {
		return systemFee;
	}

	public void setSystemFee(BigDecimal systemFee) {
		this.systemFee = systemFee;
	}

	public BigDecimal getNetToStore() {
		return netToStore;
	}

	public void setNetToStore(BigDecimal netToStore) {
		this.netToStore = netToStore;
	}

	public long getDeliveredOrders() {
		return deliveredOrders;
	}

	public void setDeliveredOrders(long deliveredOrders) {
		this.deliveredOrders = deliveredOrders;
	}

	public BigDecimal getAvgOrderValue() {
		return avgOrderValue;
	}

	public void setAvgOrderValue(BigDecimal avgOrderValue) {
		this.avgOrderValue = avgOrderValue;
	}
}
