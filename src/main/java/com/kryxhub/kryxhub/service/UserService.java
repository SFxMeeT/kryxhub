package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.*;
import com.kryxhub.kryxhub.entity.RefreshTokenEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.enums.AccountStatus;
import com.kryxhub.kryxhub.enums.Role;
import com.kryxhub.kryxhub.enums.TokenType;
import com.kryxhub.kryxhub.repository.UserRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService, RefreshTokenService refreshTokenService, OtpService otpService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    public AuthCookieAccess registerUser(RegisterRequest request) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email is already in use.");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        user.setUsername(request.getEmail().split("@")[0] + UUID.randomUUID().toString().substring(0, 5));
        user.setRole(Role.CREATOR);
        user.setAccountStatus(AccountStatus.ACTIVE);

        user = userRepository.save(user);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        String accessToken = jwtTokenService.generateToken(authentication);
        Long expiresAt = jwtTokenService.extractExpirationTime(accessToken);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        AuthAccessResponse authAccessResponse = new AuthAccessResponse(accessToken, user.getEmail(), expiresAt, TokenType.ACCESS_TOKEN);

        return new AuthCookieAccess(
                ResponseCookie.from("refresh_jwt", refreshToken.getToken())
                        .httpOnly(true)
                        .secure(true)
                        .path("/api/auth/refresh")
                        .maxAge(7 * 24 * 60 * 60)
                        .build(),
                authAccessResponse
        );
    }

    public UserProfileResponse getUserProfile(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileResponse(
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getBio(),
                user.getProfilePicUrl(),
                user.getRole().name(),
                user.getAccountStatus().name()
        );
    }

    public UserProfileResponse updateUserProfile(String email, UpdateProfileRequest request) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getDisplayName() != null && !request.getDisplayName().trim().isEmpty()) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getProfilePicUrl() != null) {
            user.setProfilePicUrl(request.getProfilePicUrl());
        }
        if (request.getPopupNotifications() != null) {
            user.setPopupNotifications(request.getPopupNotifications());
        }

        user = userRepository.save(user);

        return new UserProfileResponse(
                user.getEmail(), user.getUsername(), user.getDisplayName(),
                user.getBio(), user.getProfilePicUrl(), user.getRole().name(), user.getAccountStatus().name()
        );
    }

    public void requestAccountDeletion(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAccountStatus(AccountStatus.DELETED);
        user.setDeletionRequestedAt(OffsetDateTime.now());

        user.setDisplayName("Deleted User");
        user.setBio(null);
        user.setProfilePicUrl(null);

        userRepository.save(user);
    }

    public void requestEmailChange(String currentEmail, EmailChangeRequest request) {
        String newEmail = request.getNewEmail().toLowerCase().trim();

        if (currentEmail.equalsIgnoreCase(newEmail)) {
            throw new RuntimeException("This is already your current email address.");
        }

        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new RuntimeException("This email is already registered to another account.");
        }

        String otp = otpService.generateAndStoreOtp(newEmail, "EMAIL_CHANGE");

        emailService.sendVerificationOtp(newEmail, otp);
    }

    public AuthCookieAccess confirmEmailChange(String currentEmail, VerifyEmailChangeRequest request) {
        String newEmail = request.getNewEmail().toLowerCase().trim();

        boolean isValid = otpService.validateOtp(newEmail, "EMAIL_CHANGE", request.getOtpCode());

        if (!isValid) {
            throw new RuntimeException("Invalid or expired verification code.");
        }

        UserEntity user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(newEmail);
        user.setEmailVerified(true);
        user = userRepository.save(user);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        String newAccessToken = jwtTokenService.generateToken(authentication);
        Long expiresAt = jwtTokenService.extractExpirationTime(newAccessToken);
        RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        AuthAccessResponse authAccessResponse = new AuthAccessResponse(
                newAccessToken, user.getEmail(), expiresAt, TokenType.ACCESS_TOKEN
        );

        return new AuthCookieAccess(
                ResponseCookie.from("refresh_jwt", newRefreshToken.getToken())
                        .httpOnly(true)
                        .secure(true)
                        .path("/api/auth/refresh")
                        .maxAge(7 * 24 * 60 * 60)
                        .build(),
                authAccessResponse
        );
    }

    public void request2faEnable(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIs2faEnabled() != null && user.getIs2faEnabled()) {
            throw new RuntimeException("2FA is already enabled on this account.");
        }

        String otp = otpService.generateAndStoreOtp(email, "2FA_ENABLE");
        emailService.sendVerificationOtp(email, otp);
    }

    public void confirm2faEnable(String email, Verify2faEnableRequest request) {
        boolean isValid = otpService.validateOtp(email, "2FA_ENABLE", request.getOtpCode());

        if (!isValid) {
            throw new RuntimeException("Invalid or expired 2FA code.");
        }

        UserEntity user = userRepository.findByEmail(email).get();
        user.setIs2faEnabled(true);
        userRepository.save(user);
    }
}
