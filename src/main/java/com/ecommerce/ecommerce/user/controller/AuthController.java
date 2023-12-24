package com.ecommerce.ecommerce.user.controller;

import com.ecommerce.ecommerce.user.config.AuthRequest;
import com.ecommerce.ecommerce.user.repository.UserRepository;
import com.ecommerce.ecommerce.user.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        return userRepository.findByUsername(authRequest.getUsername())
                .flatMap(user -> {
                    if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                        String token = JwtUtil.generateToken(user.getUsername());
                        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                                .httpOnly(true)
                                .path("/")
                                .maxAge(24 * 60 * 60) // expires in 1 day
                                .build();
                        response.addHeader("Set-Cookie", cookie.toString());
                        return Mono.just(ResponseEntity.ok().build());
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                    }
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<?>> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) // expire immediately
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return Mono.just(ResponseEntity.ok().build());
    }
}
