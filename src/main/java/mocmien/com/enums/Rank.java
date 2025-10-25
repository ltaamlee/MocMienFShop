package mocmien.com.enums;

public enum Rank {
    NEW("Mới", 0),
    BRONZE("Đồng", 1),
    SILVER("Bạc", 2),
    GOLD("Vàng", 3),
    PLATINUM("Bạch kim", 4),
    VIP("VIP", 5);

    private final String displayName;
    private final int level;

    Rank(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    // Lấy rank kế tiếp (nếu có)
    public Rank getNextRank() {
        switch (this) {
            case NEW: return BRONZE;
            case BRONZE: return SILVER;
            case SILVER: return GOLD;
            case GOLD: return PLATINUM;
            case PLATINUM: return VIP;
            default: return this;
        }
    }

    // Kiểm tra có phải rank cao nhất không
    public boolean isMaxRank() {
        return this == VIP;
    }

    // So sánh cấp độ rank
    public boolean higherThan(Rank other) {
        return this.level > other.level;
    }

    public boolean lowerThan(Rank other) {
        return this.level < other.level;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
