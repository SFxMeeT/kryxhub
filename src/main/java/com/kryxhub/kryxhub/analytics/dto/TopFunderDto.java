package com.kryxhub.kryxhub.analytics.dto;

import java.util.UUID;

public class TopFunderDto {
    private UUID userId;
    private String profilePicUrl;
    private String displayName;
    private String bio;
    private long totalPools;

    public TopFunderDto(UUID userId, String profilePicUrl, String displayName, String bio, long totalPools) {
        this.userId = userId;
        this.profilePicUrl = profilePicUrl;
        this.displayName = displayName;
        this.bio = bio;
        this.totalPools = totalPools;
    }

    public UUID getUserId() { return userId; }
    public String getProfilePicUrl() { return profilePicUrl; }
    public String getDisplayName() { return displayName; }
    public String getBio() { return bio; }
    public long getTotalPools() { return totalPools; }
}