package com.invenza.services;

import com.invenza.entities.Users;
import com.invenza.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmailService emailService;

    public String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    public void sendOtp(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("No user found with this email.");
        }

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        user.setOtpVerified(false);
        usersRepository.save(user);

        emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) return false;
        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) return false;

        boolean isValid = otp.equals(user.getOtp());
        if (isValid) {
            user.setOtpVerified(true);
            usersRepository.save(user);
        }
        return isValid;
    }
}
