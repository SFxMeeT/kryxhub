package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.*;
import com.kryxhub.kryxhub.entity.RefreshTokenEntity;
import com.kryxhub.kryxhub.enums.TokenType;
import com.kryxhub.kryxhub.repository.UserRepository;
import com.kryxhub.kryxhub.security.AuthUserDetailsService;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final AuthUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenService jwtTokenService,
                       RefreshTokenService refreshTokenService,
                       AuthUserDetailsService userDetailsService,
                       UserRepository userRepository,
                       TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        var loginToken = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(loginToken);

        String accessToken = jwtTokenService.generateToken(authentication);
        Long expiresAt = jwtTokenService.extractExpirationTime(accessToken);

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail());

        return new AuthResponse(accessToken, refreshToken.getToken(), authentication.getName(), expiresAt);
    }

    public AuthCookieAccess login(AuthRequest authRequest) {

        AuthResponse authenticate = authenticate(authRequest);

        AuthAccessResponse authAccessResponse = new AuthAccessResponse(
                authenticate.getAccessToken(),
                authenticate.getEmail(),
                authenticate.getExpiresAt(),
                TokenType.ACCESS_TOKEN
        );

        return new AuthCookieAccess(ResponseCookie.from("refresh_jwt", authenticate.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .build(),
                authAccessResponse);
    }

    public ResponseCookie logout(Jwt jwt) {

        long remainingTime = jwt.getExpiresAt().toEpochMilli() - Instant.now().toEpochMilli();

        if (remainingTime > 0) {
            tokenBlacklistService.addToBlacklist(jwt.getTokenValue(), remainingTime);
        }

        refreshTokenService.deleteByUserId(userRepository.findByEmail(jwt.getSubject()).get());
        return ResponseCookie.from("refresh_jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0)
                .build();
    }

    public AuthCookieAccess refreshToken(RefreshTokenRequest request) {
        AuthResponse authResponse = refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    String newAccessToken = jwtTokenService.generateToken(authentication);
                    Long expiresAt = jwtTokenService.extractExpirationTime(newAccessToken);

                    return new AuthResponse(newAccessToken, request.getRefreshToken(), user.getEmail(), expiresAt);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));

        AuthAccessResponse authAccessResponse = new AuthAccessResponse(
                authResponse.getAccessToken(),
                authResponse.getEmail(),
                authResponse.getExpiresAt(),
                TokenType.ACCESS_TOKEN
        );

        return new AuthCookieAccess(ResponseCookie.from("refresh_jwt", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .build(),
                authAccessResponse);
    }
}
