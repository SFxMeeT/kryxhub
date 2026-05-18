package com.kryxhub.kryxhub.analytics.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserActivityDto {
    private String username;
    private String persona;
    private List<CampaignActivity> campaigns = new ArrayList<>();
    private List<SubmissionActivity> submissions = new ArrayList<>();

    public UserActivityDto(String username, String persona) {
        this.username = username;
        this.persona = persona;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPersona() { return persona; }
    public void setPersona(String persona) { this.persona = persona; }
    public List<CampaignActivity> getCampaigns() { return campaigns; }
    public void setCampaigns(List<CampaignActivity> campaigns) { this.campaigns = campaigns; }
    public List<SubmissionActivity> getSubmissions() { return submissions; }
    public void setSubmissions(List<SubmissionActivity> submissions) { this.submissions = submissions; }

    public static class CampaignActivity {
        private UUID id;
        private String title;
        private String status;
        private BigDecimal budgetRemaining;

        public CampaignActivity(UUID id, String title, String status, BigDecimal budgetRemaining) {
            this.id = id;
            this.title = title;
            this.status = status;
            this.budgetRemaining = budgetRemaining;
        }

        public UUID getId() { return id; }
        public String getTitle() { return title; }
        public String getStatus() { return status; }
        public BigDecimal getBudgetRemaining() { return budgetRemaining; }
    }

    public static class SubmissionActivity {
        private UUID id;
        private String videoTitle;
        private String platformName;
        private String status;
        private BigDecimal totalEarned;

        public SubmissionActivity(UUID id, String videoTitle, String platformName, String status, BigDecimal totalEarned) {
            this.id = id;
            this.videoTitle = videoTitle;
            this.platformName = platformName;
            this.status = status;
            this.totalEarned = totalEarned;
        }

        public UUID getId() { return id; }
        public String getVideoTitle() { return videoTitle; }
        public String getPlatformName() { return platformName; }
        public String getStatus() { return status; }
        public BigDecimal getTotalEarned() { return totalEarned; }
    }
}