package com.invenza.security.service;

import com.invenza.entities.Role;
import com.invenza.entities.Users;
import com.invenza.repositories.UserRepository;
import com.invenza.security.jwt.JwtUtil;
import com.invenza.security.model.AuthRequest;
import com.invenza.security.model.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    /**
     * Login method that validates credentials and generates JWT token
     */
    public AuthResponse login(AuthRequest authRequest) {
        // 1. Authenticate using Spring Security
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // 2. Load the user from DB
        Optional<Users> user = userRepository.findByUsername(authRequest.getUsername());

        // 3. Generate JWT token
        String token = jwtUtil.generateToken(user.get().getUsername());

        // 4. Return token + user details
        return new AuthResponse(token, user.get().getUsername(), user.get().getRole().toString());
    }

    /**
     * Register new user with BCrypt password hashing
     */
    public void registerUser(String username, String rawPassword, Role role) {
        Users user = new Users();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword)); // OR inject PasswordEncoder bean
        user.setRole(role);
        userRepository.save(user);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Users user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}
