package com.ecommerce.ecommerce.user.controller;

import com.ecommerce.ecommerce.user.model.User;
import com.ecommerce.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setStatus("ACTIVE");
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");

        Mono<User> result = userController.createUser(user);

        StepVerifier.create(result)
                .expectNextMatches(createdUser ->
                        createdUser.getPassword().equals("encodedPassword") &&
                                createdUser.getUsername().equals(user.getUsername()))
                .verifyComplete();
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Mono.just(user));

        Mono<ResponseEntity<User>> result = userController.getUserById(1L);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getStatusCode() == HttpStatus.OK &&
                                response.getBody().equals(user))
                .verifyComplete();
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");

        User updatedUser = new User();
        updatedUser.setUsername("updatedUser");
        updatedUser.setPassword("newPassword");

        Mono<ResponseEntity<User>> result = userController.updateUser(1L, updatedUser);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getStatusCode() == HttpStatus.OK &&
                                response.getBody().getUsername().equals("updatedUser") &&
                                response.getBody().getPassword().equals("encodedPassword"))
                .verifyComplete();
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Mono.just(user));
        when(userRepository.delete(any(User.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> result = userController.deleteUser(1L);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                .verifyComplete();
    }
}
