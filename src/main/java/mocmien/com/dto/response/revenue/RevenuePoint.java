package mocmien.com.dto.response.revenue;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RevenuePoint {
	private LocalDate date; // cho daily
	private Integer year; // cho monthly
	private Integer month; // cho monthly
	private BigDecimal total;

	public RevenuePoint() {
	}

	public static RevenuePoint daily(LocalDate d, BigDecimal t) {
		RevenuePoint p = new RevenuePoint();
		p.date = d;
		p.total = t;
		return p;
	}

	public static RevenuePoint monthly(int y, int m, BigDecimal t) {
		RevenuePoint p = new RevenuePoint();
		p.year = y;
		p.month = m;
		p.total = t;
		return p;
	}

	// getters/setters
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
