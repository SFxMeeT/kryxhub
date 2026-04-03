package com.kryxhub.kryxhub.dto;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String email;
    private Long expiresAt;

    public AuthResponse(String accessToken, String refreshToken, String email, Long expiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.expiresAt = expiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
