package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.*;
import com.kryxhub.kryxhub.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthAccessResponse> login(@RequestBody AuthRequest authRequest) {

        AuthCookieAccess login = authService.login(authRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, login.getCookie().toString())
                .body(login.getAuthAccessResponse());
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authService.logout(jwt).toString())
                .body("User logged out!");
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthAccessResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

        AuthCookieAccess refresh = authService.refreshToken(request);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refresh.getCookie().toString())
                .body(refresh.getAuthAccessResponse());
    }
}