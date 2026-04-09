package com.kryxhub.kryxhub.dto;

public class SocialLoginRequest {

    private String token;
    private String code;

    public String getToken() {
        return token;
    }

    public String getCode() {
        return code;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCode(String code) {
        this.code = code;
    }
}