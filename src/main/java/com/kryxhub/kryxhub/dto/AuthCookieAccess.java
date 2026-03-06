package com.kryxhub.kryxhub.dto;

import org.springframework.http.ResponseCookie;

public class AuthCookieAccess {
    private ResponseCookie cookie;
    private AuthAccessResponse authAccessResponse;

    public AuthCookieAccess(ResponseCookie loginCookie, AuthAccessResponse authAccessResponse) {
        this.cookie = loginCookie;
        this.authAccessResponse = authAccessResponse;
    }

    public ResponseCookie getCookie() {
        return cookie;
    }

    public void setCookie(ResponseCookie cookie) {
        this.cookie = cookie;
    }

    public AuthAccessResponse getAuthAccessResponse() {
        return authAccessResponse;
    }

    public void setAuthAccessResponse(AuthAccessResponse authAccessResponse) {
        this.authAccessResponse = authAccessResponse;
    }
}
