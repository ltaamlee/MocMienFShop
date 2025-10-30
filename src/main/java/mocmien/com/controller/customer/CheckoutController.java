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
import mocmien.com.entity.Store;
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
    @Autowired private mocmien.com.service.ShippingService shippingService;

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
            
            // Tính tổng với giá khuyến mãi nếu có
            BigDecimal unitPrice = (product.getPromotionalPrice() != null && product.getPromotionalPrice().compareTo(product.getPrice()) < 0)
                    ? product.getPromotionalPrice()
                    : product.getPrice();
            total = unitPrice.multiply(BigDecimal.valueOf(quantity));
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

            // Tính tổng với giá khuyến mãi nếu có
            total = cartItems.stream()
                    .map(i -> {
                        Product p = i.getProduct();
                        BigDecimal unitPrice = (p.getPromotionalPrice() != null && p.getPromotionalPrice().compareTo(p.getPrice()) < 0)
                                ? p.getPromotionalPrice()
                                : p.getPrice();
                        return unitPrice.multiply(BigDecimal.valueOf(i.getQuantity()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            // fallback: toàn bộ giỏ
            cartItems = cartService.getCartByUser(user);
            
            // Tính tổng với giá khuyến mãi nếu có
            total = cartItems.stream()
                    .map(i -> {
                        Product p = i.getProduct();
                        BigDecimal unitPrice = (p.getPromotionalPrice() != null && p.getPromotionalPrice().compareTo(p.getPrice()) < 0)
                                ? p.getPromotionalPrice()
                                : p.getPrice();
                        return unitPrice.multiply(BigDecimal.valueOf(i.getQuantity()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal shippingFee;
        try {
            CustomerAddress useAddress = defaultAddress;
            Store storeForFee = null;
            if (productId != null) {
                Product product = productService.getProductById(productId)
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
                storeForFee = product.getStore();
            } else if (!cartItems.isEmpty()) {
                storeForFee = cartItems.get(0).getProduct().getStore();
            }
            int estWeight = cartItems.stream().mapToInt(i -> i.getQuantity() * 500).sum();
            shippingFee = (useAddress != null && storeForFee != null)
                    ? shippingService.calculateShippingFee(storeForFee, useAddress, estWeight)
                    : BigDecimal.valueOf(30000);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("shippingError", ex.getMessage());
            shippingFee = BigDecimal.ZERO;
        } catch (Exception e) {
            shippingFee = BigDecimal.valueOf(30000);
        }
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

        // Kiểm tra khoảng cách & phí vận chuyển trước khi tạo đơn
        try {
            CustomerAddress defaultAddr = addressService.getDefaultAddress(profile);
            Store storeForFee = null;
            if (productId != null) {
                Product product = productService.getProductById(productId)
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
                storeForFee = product.getStore();
            } else {
                List<CartItem> tmpItems;
                if (cartItemIds != null && !cartItemIds.isBlank()) {
                    List<Integer> ids = Arrays.stream(cartItemIds.split(","))
                            .map(String::trim)
                            .filter(s -> s.matches("\\d+"))
                            .map(Integer::parseInt)
                            .toList();
                    tmpItems = cartService.getCartByUser(user).stream().filter(i -> ids.contains(i.getId())).toList();
                } else {
                    tmpItems = cartService.getCartByUser(user);
                }
                if (!tmpItems.isEmpty()) {
                    storeForFee = tmpItems.get(0).getProduct().getStore();
                }
            }
            int estWeight = 500; // đơn giản: tối thiểu 500g
            if (storeForFee != null && defaultAddr != null) {
                shippingService.calculateShippingFee(storeForFee, defaultAddr, estWeight);
            }
        } catch (IllegalArgumentException ex) {
            return "redirect:/checkout/error?msg=OutOfRadius";
        } catch (Exception ignore) { }

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

        // Sau khi tạo order: tính lại phí ship, gán delivery và cập nhật tổng thanh toán
        try {
            CustomerAddress defaultAddr = addressService.getDefaultAddress(profile);
            Store storeForFee = (order.getStore() != null) ? order.getStore() : null;
            int estWeight = order.getOrderDetails() != null && !order.getOrderDetails().isEmpty() ?
                    order.getOrderDetails().stream().mapToInt(d -> d.getQuantity() * 500).sum() : 500;
            if (storeForFee != null && defaultAddr != null) {
                // Tìm delivery phù hợp
                mocmien.com.entity.Delivery delivery = shippingService.findDeliveryForDistance(storeForFee, defaultAddr);
                if (delivery != null) {
                    order.setDelivery(delivery);
                }
                
                // Tính phí ship
                BigDecimal fee = shippingService.calculateShippingFee(storeForFee, defaultAddr, estWeight);
                order.setShippingFee(fee);
                if (order.getAmountFromCustomer() != null) {
                    order.setAmountFromCustomer(order.getAmountFromCustomer().add(fee));
                }
                orderService.save(order);
            }
        } catch (IllegalArgumentException ex) {
            return "redirect:/checkout/error?msg=OutOfRadius";
        } catch (Exception ignore) { }

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

        System.out.println("🔔 MoMo Return - orderId: " + orderId + ", resultCode: " + resultCode);
        
        boolean ok = momoService.handleMomoReturn(orderId, resultCode);
        if (ok) {
            System.out.println("✅ MoMo thành công - redirect to success");
            removePurchasedItems(request, principal);
            return "redirect:/checkout/success";
        }
        
        System.out.println("❌ MoMo thất bại/hủy - redirect to error");
        return "redirect:/checkout/error?msg=MomoError";
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
