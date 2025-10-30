package mocmien.com.controller.customer;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import mocmien.com.dto.response.customer.MomoCreateResponseDto;
import mocmien.com.entity.CartItem;
import mocmien.com.entity.CustomerAddress;
import mocmien.com.entity.Orders;
import mocmien.com.entity.Product;
import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;
import mocmien.com.enums.OrderStatus;
import mocmien.com.enums.PaymentMethod;
import mocmien.com.service.CartService;
import mocmien.com.service.CustomerAddressService;
import mocmien.com.service.MomoService;
import mocmien.com.service.OrderService;
import mocmien.com.service.ProductService;
import mocmien.com.service.UserProfileService;
import mocmien.com.service.UserService;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired private OrderService orderService;
    @Autowired private ProductService productService;
    @Autowired private UserProfileService userProfileService;
    @Autowired private UserService userService;
    @Autowired private CustomerAddressService addressService;
    @Autowired private CartService cartService;
    @Autowired private MomoService momoService;

    // === 1) Trang thanh toán ===
    @GetMapping
    public String checkoutPage(
            @RequestParam(required = false) String selectedIds,
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false, defaultValue = "1") Integer quantity,
            Model model,
            Principal principal) {

        if (principal == null) return "redirect:/login";

        // User & profile
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        UserProfile profile = userProfileService.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ khách hàng"));

        // Địa chỉ giao hàng
        List<CustomerAddress> addresses = addressService.findByCustomer(profile);
        CustomerAddress defaultAddress = addressService.getDefaultAddress(profile);

        // Items & tổng
        List<CartItem> cartItems;
        BigDecimal total;

        if (productId != null) {
            // Mua ngay
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            CartItem temp = new CartItem();
            temp.setProduct(product);
            temp.setQuantity(quantity);
            cartItems = List.of(temp);
            total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        } else if (selectedIds != null && !selectedIds.isBlank()) {
            // Mua các item đã tick trong giỏ (id là id của CartItem)
            List<Integer> ids = Arrays.stream(selectedIds.split(","))
                    .map(String::trim)
                    .filter(s -> s.matches("\\d+"))
                    .map(Integer::parseInt)
                    .toList();

            cartItems = cartService.getCartByUser(user).stream()
                    .filter(i -> ids.contains(i.getId()))
                    .toList();

            total = cartItems.stream()
                    .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            // fallback: toàn bộ giỏ
            cartItems = cartService.getCartByUser(user);
            total = cartItems.stream()
                    .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal shippingFee = BigDecimal.valueOf(30000);
        BigDecimal grandTotal = total.add(shippingFee);

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("addresses", addresses);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("productId", productId);
        model.addAttribute("quantity", quantity);
        model.addAttribute("selectedIds", selectedIds);

        return "customer/checkout";
    }

    // === 2) Xác nhận đặt hàng ===
    @PostMapping("/confirm")
    public String confirmOrder(
            @RequestParam String receiver,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) String note,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false, defaultValue = "1") Integer quantity,
            @RequestParam(required = false) String cartItemIds,
            Principal principal,
            HttpServletRequest request) {

        if (principal == null) return "redirect:/login";

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        UserProfile profile = userProfileService.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ khách hàng"));

        Orders order;

        // Tạo order
        if (productId != null) {
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            order = orderService.createOrderFromProduct(profile, product, quantity, receiver, phone, address, note);
            request.getSession().removeAttribute("selectedIds");
        } else {
            if (cartItemIds != null && !cartItemIds.isBlank()) {
                List<Integer> ids = Arrays.stream(cartItemIds.split(","))
                        .map(String::trim)
                        .filter(s -> s.matches("\\d+"))
                        .map(Integer::parseInt)
                        .toList();

                order = orderService.createOrderFromCart(profile, receiver, phone, address, note, ids);
                request.getSession().setAttribute("selectedIds", cartItemIds);
            } else {
                // Không có danh sách id cụ thể -> lấy toàn bộ giỏ hiện tại của user
                List<CartItem> allItems = cartService.getCartByUser(user);
                if (allItems == null || allItems.isEmpty()) {
                    throw new RuntimeException("Không có sản phẩm nào được chọn để thanh toán!");
                }

                List<Integer> ids = allItems.stream()
                        .map(CartItem::getId)
                        .toList();

                order = orderService.createOrderFromCart(profile, receiver, phone, address, note, ids);
                request.getSession().setAttribute("selectedIds", ids.stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + "," + b)
                        .orElse("")
                );
            }
        }

        // COD
        if (paymentMethod.equalsIgnoreCase("COD")) {
            order.setPaymentMethod(PaymentMethod.COD);
            order.setIsPaid(false);
            order.setStatus(OrderStatus.PENDING);
            orderService.save(order);

            removePurchasedItems(request, principal);
            return "redirect:/checkout/success";
        }

        // MoMo
        if (paymentMethod.equalsIgnoreCase("QR")) {
            order.setPaymentMethod(PaymentMethod.MOMO);
            order.setIsPaid(false);
            order.setStatus(OrderStatus.PENDING);
            orderService.save(order);

            try {
                MomoCreateResponseDto momoRes = momoService.createPayment(order);
                if (momoRes != null && momoRes.getPayUrl() != null) {
                    return "redirect:" + momoRes.getPayUrl();
                } else {
                    return "redirect:/checkout/error?msg=MomoError";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/checkout/error?msg=Exception";
            }
        }

        return "redirect:/checkout/error?msg=InvalidMethod";
    }

    // === 3) MoMo redirect về ===
    @GetMapping("/momo/return")
    public String handleMomoReturn(
            @RequestParam(name = "resultCode") int resultCode,
            @RequestParam(name = "orderId") String orderId,
            HttpServletRequest request,
            Principal principal) {

        boolean ok = momoService.handleMomoReturn(orderId, resultCode);
        if (ok) {
            removePurchasedItems(request, principal);
            return "redirect:/checkout/success";
        }
        return "redirect:/checkout/error";
    }

    // === 4) Thành công ===
    @GetMapping("/success")
    public String checkoutSuccess(HttpServletRequest request, Principal principal) {
        removePurchasedItems(request, principal);
        return "customer/success";
    }

    // === 5) Lỗi ===
    @GetMapping("/error")
    public String checkoutError() {
        return "customer/checkout-error";
    }

    // === 6) Xóa item đã mua khỏi giỏ (dùng removeItem vì CartService không có clearCart) ===
    private void removePurchasedItems(HttpServletRequest request, Principal principal) {
        String selectedIds = (String) request.getSession().getAttribute("selectedIds");
        if (selectedIds == null || principal == null) return;

        List<Integer> ids = Arrays.stream(selectedIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();

        // Xóa từng CartItem theo id
        for (Integer id : ids) {
            try { cartService.removeItem(id); } catch (Exception ignore) {}
        }
        request.getSession().removeAttribute("selectedIds");
    }
}
