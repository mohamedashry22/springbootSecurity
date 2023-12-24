package com.ecommerce.ecommerce.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Connection;
import reactor.core.publisher.Mono;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final ConnectionFactory connectionFactory;

    @Value("${ecommerce.db.name}")
    private String dbName;

    public DatabaseInitializer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void run(String... args) {
        Mono.from(connectionFactory.create())
                .flatMap(connection ->
                        Mono.from(connection.createStatement("CREATE DATABASE " + dbName)
                                        .execute())
                                .doFinally(signalType -> connection.close())
                )
                .subscribe(
                        result -> System.out.println("Database created successfully"),
                        error -> System.err.println("Failed to create database: " + error.getMessage())
                );
    }

    private void closeConnection(Connection connection) {
        Mono.from(connection.close()).subscribe();
    }
}
