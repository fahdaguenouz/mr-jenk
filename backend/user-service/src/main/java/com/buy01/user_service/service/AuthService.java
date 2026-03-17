package com.buy01.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.buy01.user_service.dto.AuthResponse;
import com.buy01.user_service.dto.LoginRequest;
import com.buy01.user_service.dto.RegisterRequest;
import com.buy01.user_service.models.User;
import com.buy01.user_service.repository.UserRepository;
import com.buy01.user_service.security.JwtService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String username = request.username().trim().toLowerCase();

        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email already in use");

        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("Username already in use");

        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        userRepository.save(user);

        // Return WITHOUT token
        return buildAuthResponse(user, null);
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = request.identifier().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, request.password()));

        var user = userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        // Add custom claims (UUID and Role) to the JWT
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());

        // Explicitly declare as String to fix compiler error
        String jwtToken = jwtService.generateToken(extraClaims, user);

        return buildAuthResponse(user, jwtToken);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}