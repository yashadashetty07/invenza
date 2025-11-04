package com.invenza.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Invenza Password Reset OTP");
        message.setText("Dear user,\n\nYour OTP for password reset is: " + otp
                + "\nThis OTP is valid for 5 minutes.\n\nRegards,\nInvenza Team");
        mailSender.send(message);
    }
}
