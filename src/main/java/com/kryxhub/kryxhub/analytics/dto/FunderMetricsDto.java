package com.kryxhub.kryxhub.analytics.dto;

public class FunderMetricsDto {
    private int pendingSubmissions;
    private long totalViews;
    private String paidOut;
    private String approvalRate;

    public FunderMetricsDto(int pendingSubmissions, long totalViews, String paidOut, String approvalRate) {
        this.pendingSubmissions = pendingSubmissions;
        this.totalViews = totalViews;
        this.paidOut = paidOut;
        this.approvalRate = approvalRate;
    }

    public int getPendingSubmissions() { return pendingSubmissions; }
    public long getTotalViews() { return totalViews; }
    public String getPaidOut() { return paidOut; }
    public String getApprovalRate() { return approvalRate; }
}