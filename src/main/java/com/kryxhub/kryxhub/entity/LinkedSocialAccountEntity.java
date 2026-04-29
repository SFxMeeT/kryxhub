package com.kryxhub.kryxhub.entity;

import com.kryxhub.kryxhub.enums.Platforms;
import com.kryxhub.kryxhub.enums.SocialAccountStatus;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "linked_social_accounts", indexes = {
        @Index(name = "idx_social_user_id", columnList = "user_id"),
        @Index(name = "idx_social_platform", columnList = "platform"),
        @Index(name = "idx_social_status", columnList = "status")
})
public class LinkedSocialAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Platforms platform;

    @Column(name = "platform_username", nullable = false, length = 100)
    private String platformUsername;

    @Column(name = "verification_code", length = 20)
    private String verificationCode;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialAccountStatus status = SocialAccountStatus.LINKED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public LinkedSocialAccountEntity() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Platforms getPlatform() {
        return platform;
    }

    public void setPlatform(Platforms platform) {
        this.platform = platform;
    }

    public String getPlatformUsername() {
        return platformUsername;
    }

    public void setPlatformUsername(String platformUsername) {
        this.platformUsername = platformUsername;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public OffsetDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(OffsetDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public SocialAccountStatus getStatus() {
        return status;
    }

    public void setStatus(SocialAccountStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}