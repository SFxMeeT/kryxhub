package com.kryxhub.kryxhub.dto;

public class InitiateLinkRequest {

    private String platform;
    private String platformUsername;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformUsername() {
        return platformUsername;
    }

    public void setPlatformUsername(String platformUsername) {
        this.platformUsername = platformUsername;
    }
}
