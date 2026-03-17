package com.buy01.user_service.dto;

import com.buy01.user_service.enums.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
    String token,
    String id,
    String email,
    String firstName,
    String lastName,
    Role role
) {}