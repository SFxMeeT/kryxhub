package com.kryxhub.kryxhub.user.service;

import com.kryxhub.kryxhub.admin.dto.AdminUserDto;
import com.kryxhub.kryxhub.analytics.dto.TopFunderDto;
import com.kryxhub.kryxhub.analytics.dto.UserActivityDto;
import com.kryxhub.kryxhub.auth.dto.AuthAccessResponse;
import com.kryxhub.kryxhub.auth.dto.AuthCookieAccess;
import com.kryxhub.kryxhub.auth.dto.RegisterRequest;
import com.kryxhub.kryxhub.auth.entity.RefreshTokenEntity;
import com.kryxhub.kryxhub.auth.enums.TokenType;
import com.kryxhub.kryxhub.auth.service.JwtTokenService;
import com.kryxhub.kryxhub.auth.service.OtpService;
import com.kryxhub.kryxhub.auth.service.RefreshTokenService;
import com.kryxhub.kryxhub.campaign.entity.CampaignEntity;
import com.kryxhub.kryxhub.campaign.repository.CampaignRepository;
import com.kryxhub.kryxhub.communication.service.EmailService;
import com.kryxhub.kryxhub.core.service.S3StorageService;

import com.kryxhub.kryxhub.submission.entity.SubmissionEntity;
import com.kryxhub.kryxhub.submission.repository.SubmissionRepository;
import com.kryxhub.kryxhub.user.dto.EmailChangeRequest;
import com.kryxhub.kryxhub.user.dto.UpdateProfileRequest;
import com.kryxhub.kryxhub.user.dto.UserProfileResponse;
import com.kryxhub.kryxhub.user.dto.Verify2faEnableRequest;
import com.kryxhub.kryxhub.user.dto.VerifyEmailChangeRequest;
import com.kryxhub.kryxhub.user.entity.UserEntity;
import com.kryxhub.kryxhub.user.enums.AccountStatus;
import com.kryxhub.kryxhub.user.enums.PrimaryPersona;
import com.kryxhub.kryxhub.user.enums.Role;
import com.kryxhub.kryxhub.user.repository.UserRepository;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.stream.Collectors;
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
    private final SubmissionRepository submissionRepository;
    private final CampaignRepository campaignRepository;
    private final S3StorageService s3StorageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService, RefreshTokenService refreshTokenService, OtpService otpService, EmailService emailService, SubmissionRepository submissionRepository, CampaignRepository campaignRepository, S3StorageService s3StorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.otpService = otpService;
        this.emailService = emailService;
        this.submissionRepository = submissionRepository;
        this.campaignRepository = campaignRepository;
        this.s3StorageService = s3StorageService;
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
        user.setRole(Role.USER);
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

    public Page<AdminUserDto> getAllUsersForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<UserEntity> users = userRepository.findAll(pageable);

        return users.map(user -> new AdminUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStripeAccountId()
        ));
    }

    public String toggleUserSuspension(UUID userId) { 
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Critical Error: Cannot suspend an Admin account.");
        }

        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            user.setAccountStatus(AccountStatus.ACTIVE);
            userRepository.save(user);
            return "User suspension lifted. Account is now ACTIVE.";
        } else {
            user.setAccountStatus(AccountStatus.SUSPENDED);
            userRepository.save(user);
            return "User has been SUSPENDED.";
        }
    }

    public UserActivityDto getUserActivityTracker(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserActivityDto activityTracker = new UserActivityDto(user.getUsername(), user.getPrimaryPersona().name());

        if (user.getPrimaryPersona() == PrimaryPersona.FUNDER) {
            
            List<CampaignEntity> userCampaigns = campaignRepository.findByFunder(user);
            
            List<UserActivityDto.CampaignActivity> campaignDtos = userCampaigns.stream()
                    .map(c -> new UserActivityDto.CampaignActivity(
                            c.getId(), c.getTitle(), c.getStatus(), c.getBudgetRemaining()
                    )).collect(Collectors.toList());
                    
            activityTracker.setCampaigns(campaignDtos);
            
        } else if (user.getPrimaryPersona() == PrimaryPersona.CREATOR) {
            
            List<SubmissionEntity> userSubmissions = submissionRepository.findByCreator(user);
            
            List<UserActivityDto.SubmissionActivity> submissionDtos = userSubmissions.stream()
                    .map(s -> new UserActivityDto.SubmissionActivity(
                            s.getId(), s.getVideoTitle(), s.getPlatformName().name(), s.getStatus().name(), s.getTotalEarned()
                    )).collect(Collectors.toList());
                    
            activityTracker.setSubmissions(submissionDtos);
        }

        return activityTracker;
    }

    @Transactional
    public String updateProfilePicture(String email, MultipartFile file) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = s3StorageService.uploadFile(file, "profiles");

        user.setProfilePicUrl(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    @Transactional(readOnly = true)
    public List<TopFunderDto> getTopFundersSidebar() {

        Pageable limit = PageRequest.of(0, 5);
        return userRepository.findTopFunders(limit);
    }
}
