package com.invenza.services;

import com.invenza.entities.Users;
import com.invenza.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    @Autowired
    private UsersRepository userRepository;

    public Users getUserByUsername(String username) {
        Users currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        return currentUser;
    }

}
