package mocmien.com.dto.response.category;

public record CategoryStats(
        long total,
        long active,
        long inactive
) {}
