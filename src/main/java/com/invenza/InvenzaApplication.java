package com.invenza;

import com.invenza.security.jwt.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InvenzaApplication {

    @Autowired
    private JwtUtil jwtUtil;

    public static void main(String[] args) {
        SpringApplication.run(InvenzaApplication.class, args);
    }
}