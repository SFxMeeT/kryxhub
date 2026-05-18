package com.kryxhub.kryxhub.dto;

import java.math.BigDecimal;
import java.util.List;

public class CreatorAnalyticsDto {

    private String dateRangeDisplay;
    private List<String> linkedPlatforms; 
    private Metrics metrics;
    private List<ChartDataPoint> chartData;

    public CreatorAnalyticsDto(String dateRangeDisplay, List<String> linkedPlatforms, 
                               Metrics metrics, List<ChartDataPoint> chartData) {
        this.dateRangeDisplay = dateRangeDisplay;
        this.linkedPlatforms = linkedPlatforms;
        this.metrics = metrics;
        this.chartData = chartData;
    }

    public static class Metrics {
        public long totalViews;
        public String totalPayouts;
        public int totalSubmissions;
        public int approvedSubmissions;
        public String approvalPercentage;
        public String activeCampaignsEarning;

        public Metrics(long totalViews, String totalPayouts, int totalSubmissions, 
                       int approvedSubmissions, String approvalPercentage, String activeCampaignsEarning) {
            this.totalViews = totalViews;
            this.totalPayouts = totalPayouts;
            this.totalSubmissions = totalSubmissions;
            this.approvedSubmissions = approvedSubmissions;
            this.approvalPercentage = approvalPercentage;
            this.activeCampaignsEarning = activeCampaignsEarning;
        }
    }

    public static class ChartDataPoint {
        public String date;
        public long views;
        public BigDecimal payouts;

        public ChartDataPoint(String date, long views, BigDecimal payouts) {
            this.date = date;
            this.views = views;
            this.payouts = payouts;
        }
    }

    public String getDateRangeDisplay() { return dateRangeDisplay; }
    public List<String> getLinkedPlatforms() { return linkedPlatforms; }
    public Metrics getMetrics() { return metrics; }
    public List<ChartDataPoint> getChartData() { return chartData; }
}