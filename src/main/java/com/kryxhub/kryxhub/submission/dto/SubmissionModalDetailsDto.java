package com.kryxhub.kryxhub.submission.dto;

public class SubmissionModalDetailsDto {
    private String videoTitle;
    private String campaignTitle;
    private String status;
    private int currentViews;
    private int minViewsRequired;
    private String timeAgo;
    
    private String funderNotes;
    private String totalEarned;
    private String receivedNet;
    private String daysUntilPayout;
    private String nextPayoutNet;
    private String minPayoutThreshold;

    public SubmissionModalDetailsDto(String videoTitle, String campaignTitle, String status, 
                                     int currentViews, int minViewsRequired, String timeAgo, 
                                     String funderNotes, String totalEarned, String receivedNet, 
                                     String daysUntilPayout, String nextPayoutNet, String minPayoutThreshold) {
        this.videoTitle = videoTitle;
        this.campaignTitle = campaignTitle;
        this.status = status;
        this.currentViews = currentViews;
        this.minViewsRequired = minViewsRequired;
        this.timeAgo = timeAgo;
        this.funderNotes = funderNotes;
        this.totalEarned = totalEarned;
        this.receivedNet = receivedNet;
        this.daysUntilPayout = daysUntilPayout;
        this.nextPayoutNet = nextPayoutNet;
        this.minPayoutThreshold = minPayoutThreshold;
    }

    public String getVideoTitle() { return videoTitle; }
    public String getCampaignTitle() { return campaignTitle; }
    public String getStatus() { return status; }
    public int getCurrentViews() { return currentViews; }
    public int getMinViewsRequired() { return minViewsRequired; }
    public String getTimeAgo() { return timeAgo; }
    public String getFunderNotes() { return funderNotes; }
    public String getTotalEarned() { return totalEarned; }
    public String getReceivedNet() { return receivedNet; }
    public String getDaysUntilPayout() { return daysUntilPayout; }
    public String getNextPayoutNet() { return nextPayoutNet; }
    public String getMinPayoutThreshold() { return minPayoutThreshold; }
}