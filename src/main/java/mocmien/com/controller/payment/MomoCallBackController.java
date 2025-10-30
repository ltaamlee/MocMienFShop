package mocmien.com.controller.payment;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mocmien.com.service.MomoService;


@RestController
@RequestMapping("/api/payment/momo")
public class MomoCallBackController {

	private final MomoService momoService;

	public MomoCallBackController(MomoService momoService) {
        this.momoService = momoService;
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> payload) {
        System.out.println("📩 Callback từ MoMo:");
        payload.forEach((k, v) -> System.out.println("   " + k + ": " + v));

        String orderId = (String) payload.get("orderId");
        Object rcObj = payload.get("resultCode");

        if (orderId == null || rcObj == null) {
            return ResponseEntity.badRequest().body("Thiếu orderId hoặc resultCode");
        }

        int resultCode = rcObj instanceof Number ? ((Number) rcObj).intValue()
                                                 : Integer.parseInt(rcObj.toString());

        momoService.handleCallback(orderId, resultCode);
        return ResponseEntity.ok("OK");
    }
}

