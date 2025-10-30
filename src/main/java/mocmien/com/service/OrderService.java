package mocmien.com.service;

import mocmien.com.entity.UserProfile;

import java.util.List;

import mocmien.com.enums.OrderStatus;
import mocmien.com.entity.Orders;
import mocmien.com.entity.Product;

public interface OrderService {

	Orders createOrderFromCart(UserProfile customer, String receiver, String phone, String address, String note,
			List<Integer> cartItemIds);

	Orders createOrderFromProduct(UserProfile customer, Product product, int quantity, String receiver, String phone,
			String address, String note);

	void save(Orders order);

	List<Orders> getOrdersByCustomer(UserProfile customer);

	List<Orders> getOrdersByCustomerAndStatus(UserProfile customer, OrderStatus status);

	Orders getOrderById(String id);

}
