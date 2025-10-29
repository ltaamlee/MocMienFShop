package mocmien.com.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mocmien.com.dto.response.NotificationMessage;
import mocmien.com.entity.Log;
import mocmien.com.repository.LogRepository;
import mocmien.com.service.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final LogRepository logRepository;
	private final SimpMessagingTemplate messagingTemplate;

	public NotificationServiceImpl(LogRepository logRepository, SimpMessagingTemplate messagingTemplate) {
		this.logRepository = logRepository;
		this.messagingTemplate = messagingTemplate;
	}

	@Override
	@Transactional
	public Log createNotification(String type, String message, Integer senderId, Integer receiverId) {
		Log log = new Log();
		log.setType(type);
		log.setMessage(message);
		log.setSenderId(senderId);
		log.setReceiverId(receiverId);
		log.setTimestamp(System.currentTimeMillis());
		logRepository.save(log);

		// gửi realtime qua WebSocket
		NotificationMessage dto = new NotificationMessage(log.getType(), log.getMessage(), log.getSenderId(),
				log.getReceiverId(), log.getTimestamp());
		messagingTemplate.convertAndSend("/queue/notifications-" + receiverId, dto);

		return log;
	}

	@Override
	public void markAsRead(Long logId) {
		logRepository.findById(logId).ifPresent(l -> {
			l.setIsRead(true);
			logRepository.save(l);
		});
	}

	@Override
	public void softDelete(Long logId) {
		logRepository.findById(logId).ifPresent(l -> {
			l.setIsDeleted(true);
			logRepository.save(l);
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificationMessage> getNotificationsForUser(Integer receiverId) {
		return logRepository.findByReceiverIdAndIsDeletedFalseOrderByTimestampDesc(receiverId).stream()
				.map(l -> new NotificationMessage(l.getType(), l.getMessage(), l.getSenderId(), l.getReceiverId(),
						l.getTimestamp()))
				.collect(Collectors.toList());
	}

}
