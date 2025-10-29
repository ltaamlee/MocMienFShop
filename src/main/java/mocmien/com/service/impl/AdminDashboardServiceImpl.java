package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // <-- 1. Thêm import còn thiếu
import org.springframework.stereotype.Service;

import mocmien.com.dto.response.admin.AdminChartData;
import mocmien.com.dto.response.admin.AdminDashboardStats;
import mocmien.com.dto.response.admin.AdminRecentOrder;
import mocmien.com.entity.Orders;
import mocmien.com.entity.User; // <-- 2. Thêm import còn thiếu
import mocmien.com.entity.UserProfile; // <-- 3. Thêm import còn thiếu
import mocmien.com.enums.OrderStatus;
import mocmien.com.enums.RoleName;
import mocmien.com.repository.OrdersRepository;
// import mocmien.com.repository.RoleRepository; // <-- 4. Xóa import không dùng
import mocmien.com.repository.UserRepository;
import mocmien.com.service.AdminDashboardService;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrdersRepository orderRepository;

	@Override
	public AdminDashboardStats getAdminDashboardStats() {

		// --- 1. Tính Tổng Doanh Thu ---
		// SỬA LỖI: Hàm getAdminTotalRevenue cần tham số (theo các bước sửa lỗi trước)
		BigDecimal totalRevenue = orderRepository.getAdminTotalRevenue(OrderStatus.DELIVERED).orElse(BigDecimal.ZERO);

		// --- 2. Tính Tổng Đơn Hàng (Đã đúng) ---
		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
		long totalOrders = orderRepository.countCompletedOrdersSince(OrderStatus.DELIVERED, thirtyDaysAgo);

		// --- 3. Tính Khách Hàng Mới (Đã đúng) ---
		long newCustomers = userRepository.countNewUsersByRoleSince(RoleName.CUSTOMER, thirtyDaysAgo);

		// --- 4. Tính Tổng Đối Tác (Đã đúng) ---
		long totalPartners = userRepository.countByRole(RoleName.VENDOR);

		return new AdminDashboardStats(totalRevenue, totalOrders, newCustomers, totalPartners);
	}

	@Override
	public List<AdminRecentOrder> getRecentOrders(int limit) {

		// Sửa đổi nhỏ: Tên biến rõ ràng hơn
		Pageable pageable = PageRequest.of(0, limit);

		// 2. Gọi hàm Repository mới
		List<Orders> recentOrders = orderRepository.findRecentOrders(pageable);

		// 3. Chuyển đổi (map) từ Entity sang DTO
		return recentOrders.stream().map(this::mapToRecentOrder) // Gọi hàm helper
				.collect(Collectors.toList());
	}

	/**
	 * 📦 Hàm tiện ích để map từ Orders Entity sang DTO (Cải thiện để chống lỗi
	 * NullPointerException)
	 */
	private AdminRecentOrder mapToRecentOrder(Orders order) {

		String customerName = "Khách vãng lai"; // Tên mặc định

		// 5. CẢI TIẾN: Kiểm tra null từng bước để tránh lỗi
		UserProfile profile = order.getCustomer();
		if (profile != null) {
			User user = profile.getUser();
			if (user != null) {
				customerName = user.getUsername(); // Hoặc getFullName() nếu có
			}
		}

		return new AdminRecentOrder(order.getId(), customerName, order.getCreatedAt(), // Đảm bảo getter là
																						// getCreatedAt()
				order.getStatus(), order.getAmountFromCustomer());
	}

//	@Override
//    public AdminChartData getChartData(String type, LocalDate fromDate, LocalDate toDate) {
//        
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
//        List<String> labels = new ArrayList<>();
//        List<BigDecimal> data = new ArrayList<>();
//
//        if ("daily".equals(type)) {
//            LocalDate today = LocalDate.now();
//            for (int i = 6; i >= 0; i--) {
//                labels.add(today.minusDays(i).format(formatter));
//                data.add(BigDecimal.valueOf(Math.random() * 500000 + 100000));
//            }
//        } 
//        return new AdminChartData (labels, data);
//    }

	@Override
	public AdminChartData getChartData(String type, LocalDate fromDate, LocalDate toDate) {

		LocalDateTime startDate, endDate;
		DateTimeFormatter formatter;
		boolean isMonthly = false;
		OrderStatus status = OrderStatus.DELIVERED;

		if ("daily".equals(type)) {
			endDate = LocalDateTime.now().with(LocalTime.MAX);
			startDate = endDate.minusDays(6).with(LocalTime.MIN);
			formatter = DateTimeFormatter.ofPattern("dd/MM");
		} else if ("monthly".equals(type)) {
			isMonthly = true;
			endDate = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).with(LocalTime.MIN);
			startDate = endDate.minusMonths(12).with(LocalTime.MIN);
			formatter = DateTimeFormatter.ofPattern("MM/yyyy");
		} else { // "custom"
			startDate = (fromDate != null) ? fromDate.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
			endDate = (toDate != null) ? toDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);

			if (ChronoUnit.DAYS.between(startDate, endDate) > 90) {
				isMonthly = true;
				formatter = DateTimeFormatter.ofPattern("MM/yyyy");
			} else {
				formatter = DateTimeFormatter.ofPattern("dd/MM");
			}
		}

		Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
		if (isMonthly) {
			LocalDate currentMonth = startDate.toLocalDate().withDayOfMonth(1);
			while (currentMonth.isBefore(endDate.toLocalDate())) {
				revenueMap.put(currentMonth.format(formatter), BigDecimal.ZERO);
				currentMonth = currentMonth.plusMonths(1);
			}
		} else {
			LocalDate currentDate = startDate.toLocalDate();
			while (!currentDate.isAfter(endDate.toLocalDate())) {
				revenueMap.put(currentDate.format(formatter), BigDecimal.ZERO);
				currentDate = currentDate.plusDays(1);
			}
		}

		if (isMonthly) {
			List<Object[]> results = orderRepository.findMonthlyRevenue(status, startDate, endDate);
			for (Object[] result : results) {
				// SỬA LỖI: Dùng hàm an toàn để ép kiểu
				int year = convertToLong(result[0]).intValue();
				int month = convertToLong(result[1]).intValue();
				BigDecimal total = convertToBigDecimal(result[2]);

				String label = LocalDate.of(year, month, 1).format(formatter);
				revenueMap.put(label, total);
			}
		} else {
			List<Object[]> results = orderRepository.findDailyRevenue(status, startDate, endDate);

			for (Object[] result : results) {

				// 2. Đọc 4 giá trị (Năm, Tháng, Ngày, Tiền)
				int year = convertToLong(result[0]).intValue();
				int month = convertToLong(result[1]).intValue();
				int day = convertToLong(result[2]).intValue();
				BigDecimal total = convertToBigDecimal(result[3]); // Đọc từ result[3]

				// 3. Tái tạo lại ngày và label
				LocalDate resultDate = LocalDate.of(year, month, day);
				String label = resultDate.format(formatter);

				// 4. Cập nhật Map
				revenueMap.put(label, total);
			}
		}

		List<String> labels = new ArrayList<>(revenueMap.keySet());
		List<BigDecimal> data = new ArrayList<>(revenueMap.values());
		return new AdminChartData(labels, data);
	}

	/**
	 * 📦 Hàm tiện ích an toàn để chuyển đổi Object (từ SUM) sang BigDecimal
	 */
	private BigDecimal convertToBigDecimal(Object obj) {
		if (obj == null) {
			return BigDecimal.ZERO;
		}
		if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		}
		if (obj instanceof Number) {
			return new BigDecimal(obj.toString());
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 📦 Hàm tiện ích an toàn để chuyển đổi Object (từ YEAR/MONTH) sang Long
	 */
	private Long convertToLong(Object obj) {
		if (obj == null) {
			return 0L;
		}
		if (obj instanceof Long) {
			return (Long) obj;
		}
		if (obj instanceof Number) {
			return ((Number) obj).longValue();
		}
		return 0L;
	}

}