package mocmien.com.service;

import java.time.LocalDate;
import java.util.List;

import mocmien.com.dto.response.admin.AdminChartData;
import mocmien.com.dto.response.admin.AdminDashboardStats;
import mocmien.com.dto.response.admin.AdminRecentOrder;

public interface AdminDashboardService {
	AdminDashboardStats getAdminDashboardStats();

	List<AdminRecentOrder> getRecentOrders(int limit);
	
	AdminChartData getChartData(String type, LocalDate fromDate, LocalDate toDate);
}
