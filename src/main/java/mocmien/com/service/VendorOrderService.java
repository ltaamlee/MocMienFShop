package mocmien.com.service;

import org.springframework.data.domain.*;
import mocmien.com.dto.response.order.*;
import mocmien.com.enums.OrderStatus;

public interface VendorOrderService {
	Page<VendorOrderListItemResponse> list(Integer vendorUserId, String keyword, OrderStatus status, Pageable pageable);

	VendorOrderDetailResponse detail(Integer vendorUserId, String orderId);

	void updateStatus(Integer vendorUserId, String orderId, OrderStatus toStatus);
}
