package com.kryxhub.kryxhub.dto;

public class UserProfileResponse {

    private String email;
    private String username;
    private String displayName;
    private String bio;
    private String profilePicUrl;
    private String role;
    private String accountStatus;

    public UserProfileResponse() {}

    public UserProfileResponse(String email, String username, String displayName, String bio, String profilePicUrl, String role, String accountStatus) {
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
        this.profilePicUrl = profilePicUrl;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
