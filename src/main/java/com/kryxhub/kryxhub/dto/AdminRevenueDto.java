package com.kryxhub.kryxhub.dto;

import java.math.BigDecimal;

public class AdminRevenueDto {
    private BigDecimal totalRevenue;
    private BigDecimal totalProcessed;
    private long successfulPayoutCount;

    public AdminRevenueDto(BigDecimal totalRevenue, BigDecimal totalProcessed, long successfulPayoutCount) {
        this.totalRevenue = totalRevenue;
        this.totalProcessed = totalProcessed;
        this.successfulPayoutCount = successfulPayoutCount;
    }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getTotalProcessed() { return totalProcessed; }
    public void setTotalProcessed(BigDecimal totalProcessed) { this.totalProcessed = totalProcessed; }
    public long getSuccessfulPayoutCount() { return successfulPayoutCount; }
    public void setSuccessfulPayoutCount(long successfulPayoutCount) { this.successfulPayoutCount = successfulPayoutCount; }
}