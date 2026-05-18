package com.kryxhub.kryxhub.dto;

import java.util.List;
import java.util.UUID;

public class OverviewFeedDto {
    private UUID campaignId;
    private String funderProfilePic;
    private String funderUsername;
    private String campaignType;
    private String timeAgo; 
    private String title;
    private String cpmText;
    private String budgetText;
    private List<String> platforms;
    private String thumbnailUrl;
    private FeedStats stats;

    public OverviewFeedDto(UUID campaignId, String funderProfilePic, String funderUsername, 
                           String campaignType, String timeAgo, String title, String cpmText, 
                           String budgetText, List<String> platforms, String thumbnailUrl, FeedStats stats) {
        this.campaignId = campaignId;
        this.funderProfilePic = funderProfilePic;
        this.funderUsername = funderUsername;
        this.campaignType = campaignType;
        this.timeAgo = timeAgo;
        this.title = title;
        this.cpmText = cpmText;
        this.budgetText = budgetText;
        this.platforms = platforms;
        this.thumbnailUrl = thumbnailUrl;
        this.stats = stats;
    }

    public static class FeedStats {
        public int likes;
        public int comments;
        public int views;

        public FeedStats(int likes, int comments, int views) {
            this.likes = likes;
            this.comments = comments;
            this.views = views;
        }
    }

    public UUID getCampaignId() { return campaignId; }
    public String getFunderProfilePic() { return funderProfilePic; }
    public String getFunderUsername() { return funderUsername; }
    public String getCampaignType() { return campaignType; }
    public String getTimeAgo() { return timeAgo; }
    public String getTitle() { return title; }
    public String getCpmText() { return cpmText; }
    public String getBudgetText() { return budgetText; }
    public List<String> getPlatforms() { return platforms; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public FeedStats getStats() { return stats; }
}