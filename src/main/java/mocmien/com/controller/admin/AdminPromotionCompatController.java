package mocmien.com.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mocmien.com.service.AdminPromotionService;

@RestController
@RequestMapping("/api/promotion")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPromotionCompatController {

    @Autowired
    private AdminPromotionService adminPromotionService;

    // Compatibility routes: /api/promotion/ban/{id}
    @PatchMapping("/ban/{id}")
    public ResponseEntity<?> ban(@PathVariable Integer id) {
        try {
            adminPromotionService.banPromotion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/unban/{id}")
    public ResponseEntity<?> unban(@PathVariable Integer id) {
        try {
            adminPromotionService.unbanPromotion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}



