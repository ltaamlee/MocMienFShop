package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.entity.*;
import mocmien.com.enums.OrderStatus;
import mocmien.com.repository.*;
import mocmien.com.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrdersRepository orderRepo;
	@Autowired
	private OrderDetailRepository orderDetailRepo;
	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private CartItemRepository cartItemRepo;

	@Override
	public Orders createOrderFromCart(UserProfile customer, String receiver, String phone, String address, String note,
			List<Integer> cartItemIds) {
		Orders order = new Orders();
		order.setCustomer(customer);
		order.setStatus(OrderStatus.NEW);
		order.setPaymentMethod(null);
		order.setIsPaid(false);
		order.setNote(note);

		BigDecimal total = BigDecimal.ZERO;

		// Đảm bảo set Store cho Order trước khi lưu để tránh NULL store_id
		if (cartItemIds != null && !cartItemIds.isEmpty()) {
			Integer firstCartItemId = cartItemIds.get(0);
			CartItem firstCartItem = cartItemRepo.findById(firstCartItemId)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ: " + firstCartItemId));
			Product firstProduct = firstCartItem.getProduct();
			if (firstProduct != null && firstProduct.getStore() != null) {
				order.setStore(firstProduct.getStore());
			}
		}

		Orders savedOrder = orderRepo.save(order);

		for (Integer cartItemId : cartItemIds) {
			CartItem cartItem = cartItemRepo.findById(cartItemId)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ: " + cartItemId));

			Product product = cartItem.getProduct();

			// Store đã được set trước khi lưu order

			OrderDetail detail = new OrderDetail();
			detail.setOrder(savedOrder);
			detail.setProduct(product);
			detail.setQuantity(cartItem.getQuantity());
			detail.setPrice(product.getPrice());
			detail.setPromotionalPrice(product.getPromotionalPrice());
			orderDetailRepo.save(detail);

			// Trừ kho
			product.setStock(product.getStock() - cartItem.getQuantity());
			productRepo.save(product);

			BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
			total = total.add(lineTotal);

			cartItemRepo.delete(cartItem);
		}

		savedOrder.setAmountFromCustomer(total);
		savedOrder.setAmountToStore(total);
		orderRepo.save(savedOrder);

		return savedOrder;
	}

	@Override
	public Orders createOrderFromProduct(UserProfile customer, Product product, int quantity, String receiver,
			String phone, String address, String note) {

		Orders order = new Orders();
		order.setCustomer(customer);
		order.setStore(product.getStore());
		order.setStatus(OrderStatus.NEW);
		order.setPaymentMethod(null);
		order.setIsPaid(false);
		order.setNote(note);

		BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
		order.setAmountFromCustomer(total);
		order.setAmountToStore(total);

		Orders savedOrder = orderRepo.save(order);

		// Lưu chi tiết đơn
		OrderDetail detail = new OrderDetail();
		detail.setOrder(savedOrder);
		detail.setProduct(product);
		detail.setQuantity(quantity);
		detail.setPrice(product.getPrice());
		detail.setPromotionalPrice(product.getPromotionalPrice());
		orderDetailRepo.save(detail);

		// Trừ tồn kho
		product.setStock(product.getStock() - quantity);
		productRepo.save(product);

		return savedOrder;
	}

	@Override
	public void save(Orders order) {
		orderRepo.save(order);
	}
	
	@Override
    public List<Orders> getOrdersByCustomer(UserProfile customer) {
        return orderRepo.findByCustomerOrderByCreatedAtDesc(customer);
    }

	@Override
	public List<Orders> getOrdersByCustomerAndStatus(UserProfile customer, OrderStatus status) {
	    return orderRepo.findByCustomerAndStatusOrderByCreatedAtDesc(customer, status);
	}

    @Override
    public Orders getOrderById(String id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
    }
}
