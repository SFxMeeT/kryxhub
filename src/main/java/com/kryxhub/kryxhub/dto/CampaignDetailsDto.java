package com.kryxhub.kryxhub.dto;

import java.util.List;

public class CampaignDetailsDto {

    private HeaderInfo header;
    private SidebarInfo sidebar;
    private List<String> requirements;
    private List<PayoutInfo> payouts;
    private List<LinkInfo> contentLinks;
    private List<TopVideoInfo> topVideos;

    public CampaignDetailsDto(HeaderInfo header, SidebarInfo sidebar, List<String> requirements, 
                              List<PayoutInfo> payouts, List<LinkInfo> contentLinks, List<TopVideoInfo> topVideos) {
        this.header = header;
        this.sidebar = sidebar;
        this.requirements = requirements;
        this.payouts = payouts;
        this.contentLinks = contentLinks;
        this.topVideos = topVideos;
    }

    public static class HeaderInfo {
        public String title;
        public String type;
        public String category;
        public String cpmSummary;
        public String avgReviewTime;
        public String approvalRate;
        public String thumbnailUrl;
        public List<String> platforms;

        public HeaderInfo(String title, String type, String category, String cpmSummary, 
                          String avgReviewTime, String approvalRate, String thumbnailUrl, List<String> platforms) {
            this.title = title; this.type = type; this.category = category; this.cpmSummary = cpmSummary;
            this.avgReviewTime = avgReviewTime; this.approvalRate = approvalRate; 
            this.thumbnailUrl = thumbnailUrl; this.platforms = platforms;
        }
    }

    public static class SidebarInfo {
        public String budgetRemaining;
        public String paidOutDisplay;
        public boolean hasHighReviewBacklog;
        public String category;
        public String lastUpdated;
        public String launchedOn;

        public SidebarInfo(String budgetRemaining, String paidOutDisplay, boolean hasHighReviewBacklog, 
                           String category, String lastUpdated, String launchedOn) {
            this.budgetRemaining = budgetRemaining; this.paidOutDisplay = paidOutDisplay; 
            this.hasHighReviewBacklog = hasHighReviewBacklog; this.category = category; 
            this.lastUpdated = lastUpdated; this.launchedOn = launchedOn;
        }
    }

    public static class PayoutInfo {
        public String platformName;
        public String cpmRate;
        public String minPayout;
        public String maxPayout;

        public PayoutInfo(String platformName, String cpmRate, String minPayout, String maxPayout) {
            this.platformName = platformName; this.cpmRate = cpmRate; 
            this.minPayout = minPayout; this.maxPayout = maxPayout;
        }
    }

    public static class LinkInfo {
        public String label;
        public String url;

        public LinkInfo(String label, String url) {
            this.label = label; this.url = url;
        }
    }

    public static class TopVideoInfo {
        public int rank;
        public String views;
        public String estPayout;
        public String videoUrl;

        public TopVideoInfo(int rank, String views, String estPayout, String videoUrl) {
            this.rank = rank; this.views = views; this.estPayout = estPayout; this.videoUrl = videoUrl;
        }
    }

    public HeaderInfo getHeader() { return header; }
    public SidebarInfo getSidebar() { return sidebar; }
    public List<String> getRequirements() { return requirements; }
    public List<PayoutInfo> getPayouts() { return payouts; }
    public List<LinkInfo> getContentLinks() { return contentLinks; }
    public List<TopVideoInfo> getTopVideos() { return topVideos; }
}