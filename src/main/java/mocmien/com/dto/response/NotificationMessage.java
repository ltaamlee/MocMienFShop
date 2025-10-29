package mocmien.com.dto.response;

public record NotificationMessage(
		String type,       
	    String message,
	    Integer senderId, 
	    Integer receiverId,
	    Long timestamp
		
		) {

}
