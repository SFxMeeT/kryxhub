package com.kryxhub.kryxhub.admin.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class AdminPayoutDto {
    private UUID payoutId;
    private String creatorName;
    private String campaignTitle;
    private BigDecimal amountGross;
    private BigDecimal platformFee;
    private BigDecimal amountNet;
    private String stripeTransferId;
    private String status;

    public AdminPayoutDto(UUID payoutId, String creatorName, String campaignTitle, 
                          BigDecimal amountGross, BigDecimal platformFee, 
                          BigDecimal amountNet, String stripeTransferId, String status) {
        this.payoutId = payoutId;
        this.creatorName = creatorName;
        this.campaignTitle = campaignTitle;
        this.amountGross = amountGross;
        this.platformFee = platformFee;
        this.amountNet = amountNet;
        this.stripeTransferId = stripeTransferId;
        this.status = status;
    }

    public UUID getPayoutId() { return payoutId; }
    public void setPayoutId(UUID payoutId) { this.payoutId = payoutId; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public String getCampaignTitle() { return campaignTitle; }
    public void setCampaignTitle(String campaignTitle) { this.campaignTitle = campaignTitle; }
    public BigDecimal getAmountGross() { return amountGross; }
    public void setAmountGross(BigDecimal amountGross) { this.amountGross = amountGross; }
    public BigDecimal getPlatformFee() { return platformFee; }
    public void setPlatformFee(BigDecimal platformFee) { this.platformFee = platformFee; }
    public BigDecimal getAmountNet() { return amountNet; }
    public void setAmountNet(BigDecimal amountNet) { this.amountNet = amountNet; }
    public String getStripeTransferId() { return stripeTransferId; }
    public void setStripeTransferId(String stripeTransferId) { this.stripeTransferId = stripeTransferId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}