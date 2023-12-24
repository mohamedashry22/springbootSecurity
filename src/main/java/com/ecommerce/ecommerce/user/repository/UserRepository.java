package com.ecommerce.ecommerce.user.repository;

import com.ecommerce.ecommerce.user.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User,Long> {
    Mono<User> findByUsername(String username);
}
