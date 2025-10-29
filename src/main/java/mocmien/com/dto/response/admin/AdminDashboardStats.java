package mocmien.com.dto.response.admin;

import java.math.BigDecimal;

public record AdminDashboardStats(
		BigDecimal totalRevenue,   
	    long totalOrders,          
	    long newCustomers,       
	    long totalPartners) {
	
}
