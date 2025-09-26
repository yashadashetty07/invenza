package com.invenza.controllers;

import com.invenza.RegisterRequestDTO;
import com.invenza.entities.Role;
import com.invenza.security.model.AuthRequest;
import com.invenza.security.model.AuthResponse;
import com.invenza.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RegisterRequestDTO registerRequestDTO;

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.login(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        // Convert role string to Enum
        Role role = Role.valueOf(registerRequestDTO.getRole().toUpperCase());
        authService.registerUser(registerRequestDTO.getUsername(),
                registerRequestDTO.getPassword(),
                role);
        return ResponseEntity.ok("User registered successfully");
    }
}
