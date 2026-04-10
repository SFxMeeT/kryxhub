package com.kryxhub.kryxhub.dto;

import com.kryxhub.kryxhub.entity.UserEntity;

import java.util.UUID;

public class AdminUserResponse {

    private UUID id;
    private String email;
    private String username;
    private String role;
    private String accountStatus;
    private Boolean isEmailVerified;
    private Integer trustScore;

    public AdminUserResponse(UserEntity user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = user.getRole().name();
        this.accountStatus = user.getAccountStatus().name();
        this.isEmailVerified = user.getEmailVerified();
        this.trustScore = user.getTrustScore();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public Integer getTrustScore() {
        return trustScore;
    }
}
