package com.kryxhub.kryxhub.entity;

import com.kryxhub.kryxhub.enums.AccountStatus;
import com.kryxhub.kryxhub.enums.Role;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_role", columnList = "role"),
        @Index(name = "idx_users_status", columnList = "account_status")
})
public class UsersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "google_id", unique = true, length = 255)
    private String googleId;

    @Column(name = "discord_id", unique = true, length = 255)
    private String discordId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "profile_pic_url", columnDefinition = "TEXT")
    private String profilePicUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "stripe_account_id", length = 255)
    private String stripeAccountId;

    @Column(name = "trust_score")
    private Integer trustScore = 0;

    @Column(name = "is_2fa_enabled")
    private Boolean is2faEnabled = false;

    @Column(name = "popup_notifications")
    private Boolean popupNotifications = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "deletion_requested_at")
    private OffsetDateTime deletionRequestedAt;

    @OneToMany(mappedBy = "funder", cascade = CascadeType.ALL)
    private List<CampaignsEntity> campaigns = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkedSocialAccountsEntity> socialAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<SubmissionsEntity> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<PayoutsEntity> payouts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SupportTicketsEntity> supportTickets = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationsEntity> notifications = new ArrayList<>();

    public UsersEntity() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public void addSocialAccount(LinkedSocialAccountsEntity account) {
        socialAccounts.add(account);
        account.setUser(this);
    }

    public void removeSocialAccount(LinkedSocialAccountsEntity account) {
        socialAccounts.remove(account);
        account.setUser(null);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public void setStripeAccountId(String stripeAccountId) {
        this.stripeAccountId = stripeAccountId;
    }

    public Integer getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(Integer trustScore) {
        this.trustScore = trustScore;
    }

    public Boolean getIs2faEnabled() {
        return is2faEnabled;
    }

    public void setIs2faEnabled(Boolean is2faEnabled) {
        this.is2faEnabled = is2faEnabled;
    }

    public Boolean getPopupNotifications() {
        return popupNotifications;
    }

    public void setPopupNotifications(Boolean popupNotifications) {
        this.popupNotifications = popupNotifications;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public OffsetDateTime getDeletionRequestedAt() {
        return deletionRequestedAt;
    }

    public List<CampaignsEntity> getCampaigns() {
        return campaigns;
    }

    public List<LinkedSocialAccountsEntity> getSocialAccounts() {
        return socialAccounts;
    }

    public List<SubmissionsEntity> getSubmissions() {
        return submissions;
    }

    public List<PayoutsEntity> getPayouts() {
        return payouts;
    }

    public List<SupportTicketsEntity> getSupportTickets() {
        return supportTickets;
    }

    public List<NotificationsEntity> getNotifications() {
        return notifications;
    }
}