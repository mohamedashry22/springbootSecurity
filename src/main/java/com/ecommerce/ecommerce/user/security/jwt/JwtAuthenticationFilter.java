package com.ecommerce.ecommerce.user.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Collections;

public class JwtAuthenticationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            String authToken = token.substring(7); // Remove "Bearer " prefix

            return Mono.just(authToken)
                    .map(JwtUtil::verifyToken) // Assuming this method returns a DecodedJWT object
                    .map(jwt -> {
                        String username = jwt.getSubject();
                        // Create authentication token (you should set proper authorities based on your needs)
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                        // Set authentication in the SecurityContext
                        return auth;
                    })
                    .flatMap(auth -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                    .onErrorResume(e -> {
                        // Handle the error, e.g., log it, set response status, etc.
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        }

        // If no token is found, just continue the chain without authentication
        return chain.filter(exchange);
    }
}

