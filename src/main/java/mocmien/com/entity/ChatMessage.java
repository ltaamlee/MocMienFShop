package mocmien.com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import mocmien.com.enums.ChatAction;

@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String sender;       // username hoặc guestID
    private String receiver;     // username nhân viên, null nếu group chat
    private String content;

    @Enumerated(EnumType.STRING)
    private ChatAction action;    // MESSAGE, JOINED, LEFT, TYPING

    private boolean isInternal;  // true = nội bộ, false = chat với khách

    private LocalDateTime timestamp;

    private String shiftEmployee; // nhân viên phụ trách ca trực
    
    
}