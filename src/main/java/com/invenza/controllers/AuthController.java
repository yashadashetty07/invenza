package com.invenza.controllers;

import com.invenza.entities.Role;
import com.invenza.entities.Users;
import com.invenza.security.model.AuthRequest;
import com.invenza.security.model.AuthResponse;
import com.invenza.security.model.RegisterRequestDTO;
import com.invenza.security.service.AuthService;
import com.invenza.security.service.CustomUserDetails;
import com.invenza.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsersService usersService;

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

    @GetMapping("/me")
    public ResponseEntity<Users> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Users user = usersService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> changeRequest) {
        String oldPassword = changeRequest.get("oldPassword");
        String newPassword = changeRequest.get("newPassword");

        return authService.changePassword(userDetails.getUsername(), oldPassword, newPassword) ? ResponseEntity.ok("Password Changed Successfully") : ResponseEntity.badRequest().body("Incorrect Old Password");
    }
}
