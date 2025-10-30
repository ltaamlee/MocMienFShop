package mocmien.com.controller.shipper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mocmien.com.entity.Orders;
import mocmien.com.entity.Shipper;
import mocmien.com.enums.OrderStatus;
import mocmien.com.repository.OrdersRepository;
import mocmien.com.repository.ShipperRepository;
import mocmien.com.security.CustomUserDetails;

@RestController
@RequestMapping("/api/shipper")
@PreAuthorize("hasRole('SHIPPER')")
public class ShipperApiController {

    @Autowired private ShipperRepository shipperRepo;
    @Autowired private OrdersRepository ordersRepo;

    private Shipper me(@AuthenticationPrincipal CustomUserDetails ud) {
        return shipperRepo.findByUser_UserId(ud.getUserId()).orElseThrow();
    }

    @GetMapping("/me")
    public Map<String, Object> getMe(@AuthenticationPrincipal CustomUserDetails ud) {
        Shipper s = me(ud);
        return Map.of(
            "id", s.getId(),
            "vehicleNumber", s.getVehicleNumber(),
            "vehicleType", s.getVehicleType(),
            "license", s.getLicense(),
            "available", Boolean.TRUE.equals(s.getIsOnline())
        );
    }

    @PatchMapping("/availability")
    public ResponseEntity<?> availability(@AuthenticationPrincipal CustomUserDetails ud, @RequestBody Map<String, Object> body) {
        Shipper s = me(ud);
        boolean available = body != null && Boolean.TRUE.equals(body.get("available"));
        s.setIsOnline(available);
        shipperRepo.save(s);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/location")
    public ResponseEntity<?> updateLocation(@AuthenticationPrincipal CustomUserDetails ud, @RequestBody Map<String, Object> body) {
        Shipper s = me(ud);
        if (body != null) {
            Double lat = (body.get("lat") instanceof Number) ? ((Number)body.get("lat")).doubleValue() : null;
            Double lng = (body.get("lng") instanceof Number) ? ((Number)body.get("lng")).doubleValue() : null;
            s.setCurrentLat(lat);
            s.setCurrentLng(lng);
            shipperRepo.save(s);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders/available")
    public List<Map<String, Object>> available(@AuthenticationPrincipal CustomUserDetails ud) {
        Shipper s = me(ud);
        if (!Boolean.TRUE.equals(s.getIsOnline())) {
            return java.util.List.of();
        }
        List<Orders> list = ordersRepo.findByDelivery_IdAndStatusAndShipperIsNull(s.getDelivery().getId(), OrderStatus.CONFIRMED);
        return list.stream().map(this::toListItem).collect(Collectors.toList());
    }

    @GetMapping("/orders/assigned")
    public List<Map<String, Object>> assigned(@AuthenticationPrincipal CustomUserDetails ud) {
        Shipper s = me(ud);
        List<Orders> list = ordersRepo.findByShipper_IdAndStatusIn(s.getId(), List.of(OrderStatus.SHIPPING, OrderStatus.CONFIRMED));
        return list.stream().map(this::toListItem).collect(Collectors.toList());
    }

    @PatchMapping("/orders/{id}/accept")
    public ResponseEntity<?> accept(@AuthenticationPrincipal CustomUserDetails ud, @PathVariable String id) {
        Shipper s = me(ud);
        if (!Boolean.TRUE.equals(s.getIsOnline())) {
            return ResponseEntity.status(403).body("Vui lòng bật Trực tuyến để nhận đơn");
        }
        Orders o = ordersRepo.findById(id).orElseThrow();
        if (o.getDelivery() == null || !o.getDelivery().getId().equals(s.getDelivery().getId()))
            return ResponseEntity.status(403).build();
        if (o.getShipper() != null) return ResponseEntity.status(409).build();
        if (o.getStatus() != OrderStatus.CONFIRMED) return ResponseEntity.status(409).build();
        o.setShipper(s);
        o.setStatus(OrderStatus.SHIPPING);
        ordersRepo.save(o);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<?> updateStatus(@AuthenticationPrincipal CustomUserDetails ud, @PathVariable String id, @RequestBody Map<String, Object> body) {
        if (body == null || !(body.get("status") instanceof String)) return ResponseEntity.badRequest().build();
        String statusStr = ((String) body.get("status")).toUpperCase();
        OrderStatus to;
        try { to = OrderStatus.valueOf(statusStr); } catch (Exception e) { return ResponseEntity.badRequest().build(); }

        Shipper s = me(ud);
        Orders o = ordersRepo.findByIdAndShipper_Id(id, s.getId()).orElse(null);
        if (o == null) return ResponseEntity.status(404).build();

        OrderStatus cur = o.getStatus();
        boolean ok = (cur == OrderStatus.CONFIRMED && to == OrderStatus.SHIPPING)
                || (cur == OrderStatus.SHIPPING && (to == OrderStatus.DELIVERED || to == OrderStatus.RETURNED_REFUNDED || to == OrderStatus.CANCELED));
        if (!ok) return ResponseEntity.status(409).build();

        o.setStatus(to);
        ordersRepo.save(o);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal CustomUserDetails ud, @RequestBody Map<String, Object> body) {
        Shipper s = me(ud);
        if (body != null) {
            if (body.get("vehicleNumber") instanceof String) s.setVehicleNumber((String) body.get("vehicleNumber"));
            if (body.get("vehicleType") instanceof String) s.setVehicleType((String) body.get("vehicleType"));
            if (body.get("license") instanceof String) s.setLicense((String) body.get("license"));
            shipperRepo.save(s);
        }
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toListItem(Orders o) {
        return Map.of(
            "id", o.getId(),
            "status", o.getStatus() != null ? o.getStatus().name() : null,
            "storeName", o.getStore() != null ? o.getStore().getStoreName() : null,
            "customerName", o.getCustomer() != null ? o.getCustomer().getFullName() : null
        );
    }
}


