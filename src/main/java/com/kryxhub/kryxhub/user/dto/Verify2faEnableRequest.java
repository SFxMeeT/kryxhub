package com.kryxhub.kryxhub.user.dto;

public class Verify2faEnableRequest {
    private String otpCode;

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
