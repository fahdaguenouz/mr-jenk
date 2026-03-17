package com.buy01.api_gateway.utils;

import com.buy01.api_gateway.filter.RouteValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    // We use ObjectMapper to convert our Map into a JSON string
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {

                // Safely grab the first Authorization header
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                // If it's missing or doesn't start with "Bearer ", reject the request with our custom JSON
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return onError(exchange.getResponse(), "Unauthorized. Please provide a valid Bearer token.", exchange.getRequest().getURI().getPath());
                }

                // Extract the actual token string
                String token = authHeader.substring(7);

                try {
                    // Validate the token
                    jwtUtil.validateToken(token);
                } catch (Exception e) {
                    System.out.println("Invalid token access attempt!");
                    // If validation fails, reject with our custom JSON
                    return onError(exchange.getResponse(), "Invalid or expired token.", exchange.getRequest().getURI().getPath());
                }
            }

            // If everything is good, forward the request!
            return chain.filter(exchange);
        });
    }

    // Helper method to construct the JSON response matching your other microservices
    private Mono<Void> onError(ServerHttpResponse response, String message, String path) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);

        try {
            // Convert the Map to a JSON string bytes
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            // Fallback if JSON parsing somehow fails
            return response.setComplete();
        }
    }

    public static class Config {
        // Empty class
    }
}