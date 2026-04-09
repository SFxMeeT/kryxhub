package com.kryxhub.kryxhub.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendVerificationOtp(String toEmail, String otpCode) {

        String htmlBody = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
                + "<h2>KryxHub Security Verification</h2>"
                + "<p>You requested a code to verify your account or change your email.</p>"
                + "<h1 style='color: #FF5722; letter-spacing: 5px;'>" + otpCode + "</h1>"
                + "<p>This code will expire in <strong>10 minutes</strong>.</p>"
                + "<p style='font-size: 12px; color: gray;'>If you didn't request this, please ignore this email.</p>"
                + "</div>";

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("onboarding@resend.dev")
                .to(toEmail)
                .subject("Your KryxHub Verification Code: " + otpCode)
                .html(htmlBody)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Email successfully sent! Resend ID: " + data.getId());
        } catch (ResendException e) {
            System.err.println("CRITICAL: Failed to send email via Resend: " + e.getMessage());
            throw new RuntimeException("Failed to send verification email. Please try again later.");
        }
    }
}