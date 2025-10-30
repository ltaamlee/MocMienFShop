package mocmien.com.controller.customer;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mocmien.com.entity.Orders;
import mocmien.com.entity.UserProfile;
import mocmien.com.enums.OrderStatus;
import mocmien.com.repository.UserProfileRepository;
import mocmien.com.service.OrderService;
import mocmien.com.service.ReviewService;

@Controller
@RequestMapping("/customer/orders")
public class CustomerOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ReviewService reviewService;

    /** 🔹 Lấy thông tin khách hàng theo user đang đăng nhập */
    private UserProfile getCustomer(Principal principal) {
        if (principal == null)
            throw new RuntimeException("Người dùng chưa đăng nhập");

        String username = principal.getName();
        return userProfileRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    /** 🔹 Xem danh sách đơn hàng (có lọc trạng thái) */
    @GetMapping
    public String viewOrders(@RequestParam(value = "status", required = false) String status,
                             Principal principal, Model model) {
        UserProfile customer = getCustomer(principal);

        List<Orders> orders;
        if (status == null || status.equalsIgnoreCase("tatca")) {
            orders = orderService.getOrdersByCustomer(customer);
        } else {
            try {
                // Xử lý map status cho khớp enum
                if (status.equalsIgnoreCase("returned"))
                    status = "RETURNED_REFUNDED";
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.getOrdersByCustomerAndStatus(customer, orderStatus);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Trạng thái đơn hàng không hợp lệ: " + status);
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", (status == null) ? "tatca" : status.toLowerCase());
        return "customer/order-tracking";
    }

    /** 🔹 Xem chi tiết đơn hàng */
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable("id") String id, Principal principal, Model model) {
        UserProfile customer = getCustomer(principal);
        Orders order = orderService.getOrderById(id);

        if (!order.getCustomer().getId().equals(customer.getId()))
            throw new RuntimeException("Không thể xem đơn hàng người khác");

        model.addAttribute("order", order);
        model.addAttribute("details", order.getOrderDetails());
        return "customer/order-detail";
    }

    /** 🔹 Hủy đơn hàng */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable("id") String id, Principal principal) {
        UserProfile customer = getCustomer(principal);
        Orders order = orderService.getOrderById(id);

        if (!order.getCustomer().getId().equals(customer.getId()))
            throw new RuntimeException("Không có quyền hủy đơn này");

        if (order.getStatus() != OrderStatus.NEW && order.getStatus() != OrderStatus.PENDING)
            throw new RuntimeException("Đơn hàng đã được xử lý, không thể hủy");

        order.setStatus(OrderStatus.CANCELED);
        orderService.save(order);

        return "redirect:/customer/orders?status=pending";
    }

    /** 🔹 Yêu cầu trả hàng */
    @PostMapping("/{id}/return")
    public String requestReturn(@PathVariable("id") String id, Principal principal) {
        UserProfile customer = getCustomer(principal);
        Orders order = orderService.getOrderById(id);

        if (!order.getCustomer().getId().equals(customer.getId()))
            throw new RuntimeException("Không có quyền thao tác với đơn hàng này");

        if (order.getStatus() != OrderStatus.DELIVERED)
            throw new RuntimeException("Chỉ có thể yêu cầu trả hàng cho đơn đã giao");

        order.setStatus(OrderStatus.RETURNED_REFUNDED);
        orderService.save(order);

        return "redirect:/customer/orders?status=delivered";
    }

    /** 🔹 Gửi đánh giá (Review) */
    @PostMapping("/{id}/review")
    @ResponseBody
    public String submitReview(@PathVariable("id") String id,
                               @RequestParam("rating") int rating,
                               @RequestParam("comment") String comment,
                               Principal principal) {
        UserProfile customer = getCustomer(principal);
        Orders order = orderService.getOrderById(id);

        if (!order.getCustomer().getId().equals(customer.getId()))
            throw new RuntimeException("Không có quyền đánh giá đơn này");

        if (order.getOrderDetails().size() == 1) {
            var detail = order.getOrderDetails().get(0);
            reviewService.addReview(detail.getProduct(), customer.getUser(), rating, comment);
        } else {
            order.getOrderDetails().forEach(detail ->
                    reviewService.addReview(detail.getProduct(), customer.getUser(), rating, comment));
        }

        return "success";
    }
}
