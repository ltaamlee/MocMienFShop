package mocmien.com.service;

import java.util.List;

import mocmien.com.dto.response.NotificationMessage;
import mocmien.com.entity.Log;

public interface NotificationService {

    Log createNotification(String type, String message, Integer senderId, Integer receiverId);
        
    void markAsRead(Long logId);

	void softDelete(Long logId);

	List<NotificationMessage> getNotificationsForUser(Integer receiverId);

}
