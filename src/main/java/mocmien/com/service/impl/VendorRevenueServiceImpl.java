package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.dto.response.revenue.RecentOrderItem;
import mocmien.com.dto.response.revenue.RevenuePoint;
import mocmien.com.dto.response.revenue.RevenueSummaryResponse;
import mocmien.com.entity.Orders;
import mocmien.com.entity.Store;
import mocmien.com.enums.OrderStatus;
import mocmien.com.repository.OrdersRepository;
import mocmien.com.repository.StoreRepository;
import mocmien.com.service.VendorRevenueService;

@Service
@Transactional(readOnly = true)
public class VendorRevenueServiceImpl implements VendorRevenueService {

	private final StoreRepository storeRepo;
	private final OrdersRepository ordersRepo;

	public VendorRevenueServiceImpl(final StoreRepository storeRepo, final OrdersRepository ordersRepo) {
		this.storeRepo = storeRepo;
		this.ordersRepo = ordersRepo;
	}

	private Store requireStoreOfVendor(Integer vendorUserId) {
		return storeRepo.findByVendorUserId(vendorUserId)
				.orElseThrow(() -> new IllegalStateException("Vendor chưa có Store"));
	}

	private static LocalDateTime atStart(LocalDate d) {
		return d.atStartOfDay();
	}

	private static LocalDateTime atEndExclusive(LocalDate d) {
		return d.plusDays(1).atStartOfDay();
	}

	@Override
	public RevenueSummaryResponse summary(Integer vendorUserId, LocalDate from, LocalDate to) {
		Store s = requireStoreOfVendor(vendorUserId);
		LocalDateTime start = atStart(from);
		LocalDateTime end = atEndExclusive(to);

		BigDecimal gross = ordersRepo.sumRevenueForStoreAndRange(s.getId(), OrderStatus.DELIVERED, start, end);
		long orders = ordersRepo.countDeliveredOrdersForStoreAndRange(s.getId(), OrderStatus.DELIVERED, start, end);
		BigDecimal fee = ordersRepo.sumPlatformFeeForStoreAndRange(s.getId(), OrderStatus.DELIVERED, start, end);
		BigDecimal net = ordersRepo.sumNetToStoreForStoreAndRange(s.getId(), OrderStatus.DELIVERED, start, end);

		BigDecimal avg = (orders == 0) ? BigDecimal.ZERO
				: gross.divide(BigDecimal.valueOf(orders), 2, java.math.RoundingMode.HALF_UP);

		return new RevenueSummaryResponse(gross == null ? BigDecimal.ZERO : gross, orders, avg,
				fee == null ? BigDecimal.ZERO : fee, net == null ? BigDecimal.ZERO : net);
	}

	@Override
	public List<RevenuePoint> daily(Integer vendorUserId, LocalDate from, LocalDate to) {
		Store s = requireStoreOfVendor(vendorUserId);
		LocalDateTime start = atStart(from);
		LocalDateTime end = to.plusDays(1).atStartOfDay();

		var rows = ordersRepo.findDailyRevenueByStore(s.getId(), OrderStatus.DELIVERED, start, end);
		List<RevenuePoint> points = new ArrayList<>();
		for (Object[] r : rows) {
			int y = ((Number) r[0]).intValue();
			int m = ((Number) r[1]).intValue();
			int d = ((Number) r[2]).intValue();
			BigDecimal total = (BigDecimal) r[3];
			points.add(RevenuePoint.daily(LocalDate.of(y, m, d), total));
		}
		// fill ngày trống = 0 (để chart đẹp)
		LocalDate cur = from;
		var existing = new java.util.HashMap<LocalDate, RevenuePoint>();
		for (var p : points)
			existing.put(p.getDate(), p);
		List<RevenuePoint> filled = new ArrayList<>();
		while (!cur.isAfter(to)) {
			filled.add(existing.getOrDefault(cur, RevenuePoint.daily(cur, BigDecimal.ZERO)));
			cur = cur.plusDays(1);
		}
		filled.sort(Comparator.comparing(RevenuePoint::getDate));
		return filled;
	}

	@Override
	public List<RevenuePoint> monthly(Integer vendorUserId, int year) {
		Store s = requireStoreOfVendor(vendorUserId);
		LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
		LocalDateTime end = LocalDate.of(year + 1, 1, 1).atStartOfDay();

		var rows = ordersRepo.findMonthlyRevenueByStore(s.getId(), OrderStatus.DELIVERED, start, end);
		BigDecimal[] monthSum = new BigDecimal[13];
		for (int i = 1; i <= 12; i++)
			monthSum[i] = BigDecimal.ZERO;

		for (Object[] r : rows) {
			int m = ((Number) r[1]).intValue();
			BigDecimal total = (BigDecimal) r[2];
			monthSum[m] = total;
		}
		List<RevenuePoint> points = new ArrayList<>();
		for (int m = 1; m <= 12; m++) {
			points.add(RevenuePoint.monthly(year, m, monthSum[m]));
		}
		return points;
	}

	@Override
	public List<RecentOrderItem> recent(Integer vendorUserId, int limit) {
		Store s = requireStoreOfVendor(vendorUserId);
		var list = ordersRepo.findRecentOrdersByStore(s.getId(), PageRequest.of(0, Math.max(1, limit)));
		List<RecentOrderItem> res = new ArrayList<>();
		for (Orders o : list) {
			res.add(new RecentOrderItem(o.getId(), o.getCreatedAt(),
					o.getAmountFromCustomer() == null ? BigDecimal.ZERO : o.getAmountFromCustomer(), o.getStatus(),
					o.getIsPaid()));
		}
		return res;
	}
}
