package mocmien.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mocmien.com.dto.response.delivery.DeliveryStats;
import mocmien.com.dto.response.store.StoreStats;
import mocmien.com.service.DeliveryService;
import mocmien.com.service.StoreService;

@RestController
@RequestMapping("/api/admin/delivery")
public class AdminDeliveryController {

	@Autowired
	private DeliveryService deliveryService;
	
	// -----------------------
    // Thống kê các đơn vị vận chuyển
    // -----------------------
    @GetMapping("/stats")
    public DeliveryStats getDeliveryStatistics() {
        return deliveryService.getDeliveryStatistics();
    }
}
