package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.AdminUserUpdateRequest;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.enums.AccountStatus;
import com.kryxhub.kryxhub.enums.Role;
import com.kryxhub.kryxhub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity updateUserAsAdmin(UUID userId, AdminUserUpdateRequest request) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getBio() != null) user.setBio(request.getBio());

        if (request.getEmail() != null) {
            if (userRepository.findByEmail(request.getEmail()).isPresent() && !user.getEmail().equals(request.getEmail())) {
                throw new RuntimeException("Email already in use by another account.");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getEmailVerified() != null) user.setEmailVerified(request.getEmailVerified());
        if (request.getIs2faEnabled() != null) user.setIs2faEnabled(request.getIs2faEnabled());

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        if (request.getAccountStatus() != null) {
            user.setAccountStatus(request.getAccountStatus());
        }
        if (request.getTrustScore() != null) user.setTrustScore(request.getTrustScore());
        if (request.getStripeAccountId() != null) user.setStripeAccountId(request.getStripeAccountId());

        return userRepository.save(user);
    }
}
