package com.kryxhub.kryxhub.dto;

import java.util.UUID;

public class CreatorSubmissionResponseDto {
    private UUID submissionId;
    private String videoTitle;
    private String videoUrl;
    private String campaignTitle;
    private String creatorUsername;
    private String status; 
    private int currentViews;
    private int minViewsRequired;
    private String submittedDate;
    private String estimatedPayout;

    public CreatorSubmissionResponseDto(UUID submissionId, String videoTitle, String videoUrl, 
                                        String campaignTitle, String creatorUsername, String status, 
                                        int currentViews, int minViewsRequired, String submittedDate, 
                                        String estimatedPayout) {
        this.submissionId = submissionId;
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.campaignTitle = campaignTitle;
        this.creatorUsername = creatorUsername;
        this.status = status;
        this.currentViews = currentViews;
        this.minViewsRequired = minViewsRequired;
        this.submittedDate = submittedDate;
        this.estimatedPayout = estimatedPayout;
    }

    public UUID getSubmissionId() { return submissionId; }
    public String getVideoTitle() { return videoTitle; }
    public String getVideoUrl() { return videoUrl; }
    public String getCampaignTitle() { return campaignTitle; }
    public String getCreatorUsername() { return creatorUsername; }
    public String getStatus() { return status; }
    public int getCurrentViews() { return currentViews; }
    public int getMinViewsRequired() { return minViewsRequired; }
    public String getSubmittedDate() { return submittedDate; }
    public String getEstimatedPayout() { return estimatedPayout; }
}