package com.buy01.api_gateway.filter;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Define public endpoints that bypass JWT validation
   public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/eureka",
            "/v3/api-docs",      // <-- ADD THIS
            "/swagger-ui",       // <-- ADD THIS
            "/swagger-ui.html"   // <-- ADD THIS
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}