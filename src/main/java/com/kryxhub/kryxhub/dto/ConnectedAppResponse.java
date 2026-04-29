package com.kryxhub.kryxhub.dto;

import com.kryxhub.kryxhub.enums.Platforms;

public class ConnectedAppResponse {

    private Platforms platform;
    private String platformUsername;
    private Boolean isVerified;
    private String verificationCode;

    public ConnectedAppResponse(Platforms platform, String platformUsername, Boolean isVerified, String verificationCode) {
        this.platform = platform;
        this.platformUsername = platformUsername;
        this.isVerified = isVerified;
        this.verificationCode = verificationCode;
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

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
