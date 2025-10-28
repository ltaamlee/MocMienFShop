package mocmien.com.dto.response.users;

public record UserStats(
        long totalUsers,
        long activeUsers,
        long inactiveUsers,
        long blockedUsers
) {}
