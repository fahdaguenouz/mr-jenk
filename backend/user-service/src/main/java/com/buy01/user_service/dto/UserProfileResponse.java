package com.buy01.user_service.dto;

import com.buy01.user_service.enums.Role;
import lombok.Builder;

@Builder
public record UserProfileResponse(
    String id,
    String email,
    String username,
    String firstName,
    String lastName,
    Role role,
    String avatarMediaId
) {}