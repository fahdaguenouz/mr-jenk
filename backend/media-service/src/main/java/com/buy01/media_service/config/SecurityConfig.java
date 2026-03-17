package com.buy01.media_service.config;

// 1. ADD THESE IMPORTS
import com.buy01.media_service.security.JwtAuthenticationEntryPoint;
import com.buy01.media_service.security.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Allows @PreAuthorize("hasRole('SELLER')")
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    // 2. INJECT YOUR JWT FILTER
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler) // Inject our custom 401 handler here!
                )
                // 3. MAKE SESSION STATELESS (Crucial for JWT microservices)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/media/upload").hasRole("SELLER") // Only Sellers can upload
                        // In SecurityConfig.java for User, Product, and Media
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/media/images/**").permitAll() // Anyone can view images
                        .anyRequest().authenticated())
                // 4. ADD YOUR FILTER BEFORE THE DEFAULT SPRING SECURITY FILTER
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}