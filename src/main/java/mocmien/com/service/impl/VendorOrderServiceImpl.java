package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mocmien.com.dto.response.order.*;
import mocmien.com.entity.*;
import mocmien.com.enums.OrderStatus;
import mocmien.com.repository.*;
import mocmien.com.service.VendorOrderService;

@Service
@Transactional
@RequiredArgsConstructor
public class VendorOrderServiceImpl implements VendorOrderService {

	private final StoreRepository storeRepo;
	private final OrdersRepository ordersRepo;

    

	private Store requireStoreOfVendor(Integer vendorUserId) {
		return storeRepo.findByVendorUserId(vendorUserId)
				.orElseThrow(() -> new IllegalStateException("Vendor chưa có Store"));
	}

	private Store findStoreOfVendor(Integer vendorUserId) {
		return storeRepo.findByVendorUserId(vendorUserId).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<VendorOrderListItemResponse> list(Integer vendorUserId, String keyword, OrderStatus status,
			Pageable pageable) {
		Store store = findStoreOfVendor(vendorUserId);
		if (store == null) {
			// không ném lỗi -> trả về trang rỗng
			return new PageImpl<>(List.of(), pageable, 0);
		}
		Page<Orders> page;
		if (status != null) {
			page = ordersRepo.findByStore_IdAndStatusIn(store.getId(), List.of(status), pageable);
		} else {
			page = ordersRepo.findByStore_Id(store.getId(), pageable);
		}

		final String kw = (keyword == null) ? "" : keyword.toLowerCase().trim();
		var filtered = page.getContent().stream()
				.filter(o -> kw.isEmpty() || (o.getId() != null && o.getId().toLowerCase().contains(kw))
						|| (o.getCustomer() != null && o.getCustomer().getFullName() != null
								&& o.getCustomer().getFullName().toLowerCase().contains(kw)))
				.map(this::toListItem).collect(Collectors.toList());
		return new PageImpl<>(filtered, pageable, page.getTotalElements());
	}

	private VendorOrderListItemResponse toListItem(Orders o) {
		VendorOrderListItemResponse r = new VendorOrderListItemResponse();
		r.setId(o.getId());
		r.setCustomerName(o.getCustomer() != null ? o.getCustomer().getFullName() : "(Khách)");
		r.setCreatedAt(o.getCreatedAt());
        // Tính tổng = tổng tiền hàng (ưu tiên giá khuyến mãi nếu có) + phí vận chuyển
        BigDecimal itemsTotal = BigDecimal.ZERO;
        if (o.getOrderDetails() != null) {
            itemsTotal = o.getOrderDetails().stream()
                    .map(d -> {
                        BigDecimal unit = d.getPromotionalPrice() != null ? d.getPromotionalPrice() : d.getPrice();
                        BigDecimal qty = d.getQuantity() == null ? BigDecimal.ZERO : BigDecimal.valueOf(d.getQuantity());
                        return (unit == null ? BigDecimal.ZERO : unit).multiply(qty);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        BigDecimal shippingFee = (o.getShippingFee() != null) ? o.getShippingFee() : BigDecimal.ZERO;
        r.setTotal(itemsTotal.add(shippingFee));
		r.setPaymentMethodDisplay(o.getPaymentMethod() != null ? o.getPaymentMethod().getDisplayName() : "—");
        r.setPaid(Boolean.TRUE.equals(o.getIsPaid()));
		r.setStatus(o.getStatus());
		return r;
	}

	@Override
	@Transactional(readOnly = true)
	public VendorOrderDetailResponse detail(Integer vendorUserId, String orderId) {
		Store store = findStoreOfVendor(vendorUserId);
		if (store == null)
			throw new IllegalArgumentException("Vendor chưa có store");
		Orders o = ordersRepo.findByIdAndStore_Id(orderId, store.getId())
				.orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

		VendorOrderDetailResponse d = new VendorOrderDetailResponse();
		d.setId(o.getId());
		d.setCustomerName(o.getCustomer() != null ? o.getCustomer().getFullName() : null);
		d.setCustomerPhone(o.getCustomer() != null ? o.getCustomer().getUser().getPhone() : null);
		d.setShippingAddress( /* nếu có bảng address thì map vào đây */ null);

		d.setPaymentMethodDisplay(o.getPaymentMethod() != null ? o.getPaymentMethod().getDisplayName() : "—");
		d.setPaid(Boolean.TRUE.equals(o.getIsPaid()));
		d.setStatus(o.getStatus());
		d.setCreatedAt(o.getCreatedAt());
		d.setUpdatedAt(o.getUpdatedAt());

		BigDecimal itemsTotal = BigDecimal.ZERO;

		if (o.getOrderDetails() != null) {
			var items = o.getOrderDetails().stream().map(od -> {
				var it = new VendorOrderDetailResponse.Item();
				it.setProductId(od.getProduct() != null ? od.getProduct().getId() : null);
				it.setProductName(od.getProduct() != null ? od.getProduct().getProductName() : null);
				it.setQuantity(od.getQuantity());
                // Giá hiển thị: giá gốc
                it.setPrice(od.getPrice());
                // Thành tiền: theo giá sau khuyến mãi (nếu có), ngược lại dùng giá gốc
                BigDecimal unit = od.getPromotionalPrice() != null ? od.getPromotionalPrice() : od.getPrice();
                BigDecimal amt = (unit == null || od.getQuantity() == null) ? BigDecimal.ZERO
                        : unit.multiply(BigDecimal.valueOf(od.getQuantity()));
				it.setAmount(amt);
				return it;
			}).collect(Collectors.toList());
			d.setItems(items);
            itemsTotal = items.stream().map(VendorOrderDetailResponse.Item::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
		}

		d.setItemsTotal(itemsTotal);
        BigDecimal shippingFee = (o.getShippingFee() != null) ? o.getShippingFee() : BigDecimal.ZERO;
		d.setShippingFee(shippingFee);
		d.setGrandTotal(itemsTotal.add(shippingFee != null ? shippingFee : BigDecimal.ZERO));
		return d;
	}

	@Override
	public void updateStatus(Integer vendorUserId, String orderId, OrderStatus toStatus) {
		Store store = findStoreOfVendor(vendorUserId);
		if (store == null)
			throw new IllegalArgumentException("Vendor chưa có store");
		if (toStatus == null)
			throw new IllegalArgumentException("Trạng thái đích không hợp lệ");

		Orders o = ordersRepo.findByIdAndStore_Id(orderId, store.getId())
				.orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

		// CHỈ cho chuyển: NEW -> PENDING -> CONFIRMED
		OrderStatus cur = o.getStatus();
		boolean ok = (cur == OrderStatus.NEW && toStatus == OrderStatus.PENDING)
				|| (cur == OrderStatus.PENDING && toStatus == OrderStatus.CONFIRMED);

		if (!ok)
			throw new IllegalStateException("Không thể chuyển trạng thái từ " + cur + " sang " + toStatus);

		o.setStatus(toStatus);
		ordersRepo.save(o);
	}
}
