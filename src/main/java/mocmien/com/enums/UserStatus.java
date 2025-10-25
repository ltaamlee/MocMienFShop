package mocmien.com.enums;

public enum UserStatus {
    ONLINE(1, "Đang hoạt động"),
    OFFLINE(0, "Ngoại tuyến");

    private final int value;
    private final String displayName;

    UserStatus(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Kiểm tra user đang online không
    public boolean isOnline() {
        return this == ONLINE;
    }

    // Kiểm tra user đang offline không
    public boolean isOffline() {
        return this == OFFLINE;
    }

    // Chuyển từ int sang enum
    public static UserStatus fromValue(int value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid user status value: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}