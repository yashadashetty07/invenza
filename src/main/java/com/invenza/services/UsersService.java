package com.invenza.services;

import com.invenza.entities.Users;
import com.invenza.repositories.UsersRepository;
import com.invenza.security.model.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users getUserByUsername(String username) {
        Users currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        return currentUser;
    }

    public String changePassword(String username, ChangePasswordRequest request) {
        // ✅ Find user
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // ✅ Validate old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect old password");
        }

        // ✅ Set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password changed successfully for user: " + username;
    }
}
