package mocmien.com.enums;

public enum ChatAction {

    MESSAGE("Nhắn tin"),       
    JOINED("Tham gia"),        
    LEFT("Rời khỏi"),          
    TYPING("Đang nhắn tin");   

    private final String displayName;

    ChatAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
