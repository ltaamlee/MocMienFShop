package mocmien.com.dto.response.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import mocmien.com.enums.OrderStatus;

public record AdminRecentOrder(
		String id,
	    String customerName,
	    LocalDateTime createdAt,
	    OrderStatus status, 
	    BigDecimal totalAmount
		) {

}
