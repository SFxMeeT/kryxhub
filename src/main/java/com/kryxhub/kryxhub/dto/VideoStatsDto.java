package com.kryxhub.kryxhub.dto;

import java.time.OffsetDateTime;

public class VideoStatsDto {
    private String videoTitle;
    private Integer viewCount;
    private OffsetDateTime uploadedAt;

    public VideoStatsDto(String videoTitle, Integer viewCount, OffsetDateTime uploadedAt) {
        this.videoTitle = videoTitle;
        this.viewCount = viewCount;
        this.uploadedAt = uploadedAt;
    }

    public String getVideoTitle() { return videoTitle; }
    public Integer getViewCount() { return viewCount; }
    public OffsetDateTime getUploadedAt() { return uploadedAt; }
    
}