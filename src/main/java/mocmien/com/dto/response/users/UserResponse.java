package mocmien.com.dto.response.users;

import java.time.LocalDateTime;

import mocmien.com.enums.UserStatus;

public record UserResponse(
        Integer userId,
        String username,
        String fullName,
        String email,
        String phone,
        String roleName,
        UserStatus status,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt
) {}