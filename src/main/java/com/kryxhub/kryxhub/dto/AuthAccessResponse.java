package com.kryxhub.kryxhub.dto;

import com.kryxhub.kryxhub.enums.TokenType;

public class AuthAccessResponse {
    private String accessToken;
    private String email;
    private Long expiresAt;
    private TokenType tokenType;

    public AuthAccessResponse(String accessToken, String email, Long expiresAt, TokenType tokenType) {
        this.accessToken = accessToken;
        this.email = email;
        this.expiresAt = expiresAt;
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmail() {
        return email;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}
