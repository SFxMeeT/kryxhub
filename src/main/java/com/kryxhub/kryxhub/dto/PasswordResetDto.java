package com.kryxhub.kryxhub.dto;

public class PasswordResetDto {

    public static class ForgotPasswordRequest {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class VerifyOtpRequest {
        private String email;
        private String otp;
        public String getEmail() { return email; }
        public String getOtp() { return otp; }
    }

    public static class ResetPasswordRequest {
        private String email;
        private String resetToken;
        private String newPassword;
        public String getEmail() { return email; }
        public String getResetToken() { return resetToken; }
        public String getNewPassword() { return newPassword; }
    }
}