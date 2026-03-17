package com.buy01.user_service.dto;

import com.buy01.user_service.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "Last name is required") String lastName,
    @NotBlank(message = "Username is required") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String username,
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password,
    @NotNull(message = "Role is required") Role role
) {}