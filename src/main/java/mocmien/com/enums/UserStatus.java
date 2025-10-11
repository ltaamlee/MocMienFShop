package mocmien.com.enums;

public enum UserStatus {

	ACTIVE(1, "Hoạt động"), INACTIVE(0, "Không hoạt động"), BLOCKED(-1, "Bị khóa");

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

	// Chuyển từ int sang enum
	public static UserStatus fromValue(int value) {
		for (UserStatus status : UserStatus.values()) {
			if (status.value == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid user status value: " + value);
	}

	// Kiểm tra user có thể đăng nhập không
	public boolean canLogin() {
		return this == ACTIVE;
	}

	// Kiểm tra user có bị khóa không
	public boolean isBlocked() {
		return this == BLOCKED;
	}

	// Kiểm tra user có đang hoạt động không
	public boolean isActive() {
		return this == ACTIVE;
	}
}
