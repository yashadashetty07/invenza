package com.invenza.controllers;

import com.invenza.entities.Users;
import com.invenza.repositories.UsersRepository;
import com.invenza.security.model.ChangePasswordRequest;
import com.invenza.security.model.LoginRequest;
import com.invenza.security.model.LoginResponse;
import com.invenza.security.service.AuthService;
import com.invenza.security.service.CustomUserDetails;
import com.invenza.services.OtpService;
import com.invenza.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private UsersRepository usersRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        otpService.sendOtp(email);
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + email));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        boolean verified = otpService.verifyOtp(email, otp);
        if (verified)
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        else
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and new password are required"));
        }

        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        if (!user.isOtpVerified() || user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "OTP expired or not verified"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpVerified(false);
        user.setOtpExpiry(null);
        user.setPasswordChangedAt(java.time.LocalDateTime.now());
        usersRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<Users> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Users user = usersService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
    @PostMapping("/change-password")
    public ResponseEntity <?> changePassword(@RequestBody ChangePasswordRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        usersService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}