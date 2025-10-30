package mocmien.com.service;

import java.time.LocalDate;
import java.util.List;
import mocmien.com.dto.response.revenue.RevenuePoint;
import mocmien.com.dto.response.revenue.RevenueSummaryResponse;
import mocmien.com.dto.response.revenue.RecentOrderItem;

public interface VendorRevenueService {
	RevenueSummaryResponse summary(Integer vendorUserId, LocalDate from, LocalDate to);

	List<RevenuePoint> daily(Integer vendorUserId, LocalDate from, LocalDate to);

	List<RevenuePoint> monthly(Integer vendorUserId, int year);

	List<RecentOrderItem> recent(Integer vendorUserId, int limit);
}
