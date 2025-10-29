package mocmien.com.controller.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mocmien.com.dto.response.admin.AdminChartData;
import mocmien.com.dto.response.admin.AdminDashboardStats;
import mocmien.com.dto.response.admin.AdminRecentOrder;
import mocmien.com.service.AdminDashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
/*
 * @PreAuthorize("hasRole('ADMIN')")
 */public class AdminDashboardController {
	@Autowired
    private AdminDashboardService dashboardService;
	
	@GetMapping("/stats")
    public AdminDashboardStats getAdminStats() {
        return dashboardService.getAdminDashboardStats();
    }
	
	@GetMapping("/recent-orders")
    public List<AdminRecentOrder> getRecentOrders(
            @RequestParam(defaultValue = "5") int limit) { 
        
        return dashboardService.getRecentOrders(limit);
    }
	
	@GetMapping("/chart")
    public ResponseEntity<AdminChartData> getChartData(
            @RequestParam String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        return ResponseEntity.ok(dashboardService.getChartData(type, fromDate, toDate));
    }
}
