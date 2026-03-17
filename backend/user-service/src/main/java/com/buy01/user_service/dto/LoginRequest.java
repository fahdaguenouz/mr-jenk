package com.buy01.user_service.dto;


import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
       @NotBlank(message = "Email or Username is required") String identifier,
       @NotBlank(message = "Password is required") String password
) {}