package com.kryxhub.kryxhub.dto;

import java.util.List;

public class FunderAnalyticsDto {

    private String dateRangeDisplay;
    private Metrics metrics;
    private List<ChartDataPoint> chartData;

    public FunderAnalyticsDto(String dateRangeDisplay, Metrics metrics, List<ChartDataPoint> chartData) {
        this.dateRangeDisplay = dateRangeDisplay;
        this.metrics = metrics;
        this.chartData = chartData;
    }

    public static class Metrics {
        private long totalViews;
        private String totalPayouts;
        private String effectiveCpm;
        private String originalCpm;
        private String cpmBadge;
        private int totalSubmissions;
        private int approvedSubmissions;
        private String approvalPercentage;

        public Metrics(long totalViews, String totalPayouts, String effectiveCpm, String originalCpm, 
                       String cpmBadge, int totalSubmissions, int approvedSubmissions, String approvalPercentage) {
            this.totalViews = totalViews;
            this.totalPayouts = totalPayouts;
            this.effectiveCpm = effectiveCpm;
            this.originalCpm = originalCpm;
            this.cpmBadge = cpmBadge;
            this.totalSubmissions = totalSubmissions;
            this.approvedSubmissions = approvedSubmissions;
            this.approvalPercentage = approvalPercentage;
        }

        public long getTotalViews() { return totalViews; }
        public String getTotalPayouts() { return totalPayouts; }
        public String getEffectiveCpm() { return effectiveCpm; }
        public String getOriginalCpm() { return originalCpm; }
        public String getCpmBadge() { return cpmBadge; }
        public int getTotalSubmissions() { return totalSubmissions; }
        public int getApprovedSubmissions() { return approvedSubmissions; }
        public String getApprovalPercentage() { return approvalPercentage; }
    }

    public static class ChartDataPoint {
        private String date; 
        private long views;
        private long likes;
        private long comments;
        private long shares;

        public ChartDataPoint(String date, long views, long likes, long comments, long shares) {
            this.date = date;
            this.views = views;
            this.likes = likes;
            this.comments = comments;
            this.shares = shares;
        }

        public String getDate() { return date; }
        public long getViews() { return views; }
        public long getLikes() { return likes; }
        public long getComments() { return comments; }
        public long getShares() { return shares; }
    }

    public String getDateRangeDisplay() { return dateRangeDisplay; }
    public Metrics getMetrics() { return metrics; }
    public List<ChartDataPoint> getChartData() { return chartData; }
}