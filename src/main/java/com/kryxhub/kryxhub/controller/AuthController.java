package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.*;
import com.kryxhub.kryxhub.exception.Requires2faException;
import com.kryxhub.kryxhub.service.AuthService;
import com.kryxhub.kryxhub.service.UserService;
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
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            AuthCookieAccess login = authService.login(authRequest);

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, login.getCookie().toString())
                    .body(login.getAuthAccessResponse());

        } catch (Requires2faException e) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("{\"requires2fa\": true, \"message\": \"Please enter the code sent to your email.\"}");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Invalid email or password.\"}");
        }
    }

    @PostMapping("/login/verify-2fa")
    public ResponseEntity<?> verify2faLogin(@RequestBody Verify2faLoginRequest request) {
        try {
            AuthCookieAccess login = authService.verify2faLogin(request);

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, login.getCookie().toString())
                    .body(login.getAuthAccessResponse());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthCookieAccess authCookieAccess = userService.registerUser(request);

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, authCookieAccess.getCookie().toString())
                    .body(authCookieAccess.getAuthAccessResponse());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authService.logout(jwt).toString())
                .body("User logged out!");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthAccessResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

        AuthCookieAccess refresh = authService.refreshToken(request);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refresh.getCookie().toString())
                .body(refresh.getAuthAccessResponse());
    }

    @PostMapping("/social/google")
    public ResponseEntity<AuthAccessResponse> handleGoogleLogin(@RequestBody SocialLoginRequest request) {
        AuthCookieAccess authGoogle = authService.authGoogleLogin(request.getToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authGoogle.getCookie().toString())
                .body(authGoogle.getAuthAccessResponse());
    }

    @PostMapping("/social/discord")
    public ResponseEntity<AuthAccessResponse> handleDiscordLogin(@RequestBody SocialLoginRequest request) {
        AuthCookieAccess authDiscord = authService.authDiscordLogin(request.getCode());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authDiscord.getCookie().toString())
                .body(authDiscord.getAuthAccessResponse());
    }
}