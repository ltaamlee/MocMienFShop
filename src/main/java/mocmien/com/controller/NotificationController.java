package mocmien.com.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import mocmien.com.dto.response.NotificationMessage;
import mocmien.com.entity.Log;
import mocmien.com.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Lấy tất cả notification của user
    @GetMapping("/{receiverId}")
    public List<NotificationMessage> getNotifications(@PathVariable Integer receiverId) {
        return notificationService.getNotificationsForUser(receiverId);
    }

    // Tạo notification mới (có thể là shop, chat, order...)
    @PostMapping
    public Log createNotification(@RequestParam String type,
                                  @RequestParam String message,
                                  @RequestParam Integer senderId,
                                  @RequestParam Integer receiverId) {
        return notificationService.createNotification(type, message, senderId, receiverId);
    }

    // Đánh dấu đã đọc
    @PatchMapping("/{logId}/read")
    public void markAsRead(@PathVariable Long logId) {
        notificationService.markAsRead(logId);
    }

    // Xóa mềm
    @DeleteMapping("/{logId}")
    public void softDelete(@PathVariable Long logId) {
        notificationService.softDelete(logId);
    }
}
