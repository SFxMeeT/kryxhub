package com.kryxhub.kryxhub.dto;

public class UpdateProfileRequest {

    private String displayName;
    private String bio;
    private String profilePicUrl;
    private Boolean popupNotifications;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public Boolean getPopupNotifications() {
        return popupNotifications;
    }

    public void setPopupNotifications(Boolean popupNotifications) {
        this.popupNotifications = popupNotifications;
    }
}