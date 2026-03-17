package com.buy01.product_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            if (jwtService.isTokenValid(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Extract the custom claims we embedded in the User Service
                String userId = jwtService.extractUserId(jwt);
                String role = jwtService.extractRole(jwt);

                // Spring Security requires roles to start with "ROLE_" for the @PreAuthorize annotation to work
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // We use the userId as the "Principal" so our controllers can easily grab it!
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId, 
                        null, 
                        Collections.singletonList(authority)
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception ex) {
            logger.error("Invalid JWT token", ex);
        }

        filterChain.doFilter(request, response);
    }
}