package com.buy01.user_service.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "Last name is required") String lastName,
    String avatarMediaId 
) {}