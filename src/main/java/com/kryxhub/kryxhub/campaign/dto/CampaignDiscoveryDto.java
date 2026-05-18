package com.kryxhub.kryxhub.campaign.dto;

import java.util.List;
import java.util.UUID;

public class CampaignDiscoveryDto {
    private UUID id;
    private String funderProfilePic;
    private String type;
    private String category;
    private String timeAgo;
    private String title;
    
    private List<String> platforms;
    
    private String paidOutDisplay;
    private String totalBudgetDisplay;
    private String cpmDisplay;
    
    private String approvalPercentage;
    private String totalViewsDisplay;
    private long creatorsCount;

    public CampaignDiscoveryDto(UUID id, String funderProfilePic, String type, String category, 
                                String timeAgo, String title, List<String> platforms, 
                                String paidOutDisplay, String totalBudgetDisplay, String cpmDisplay, 
                                String approvalPercentage, String totalViewsDisplay, long creatorsCount) {
        this.id = id;
        this.funderProfilePic = funderProfilePic;
        this.type = type;
        this.category = category;
        this.timeAgo = timeAgo;
        this.title = title;
        this.platforms = platforms;
        this.paidOutDisplay = paidOutDisplay;
        this.totalBudgetDisplay = totalBudgetDisplay;
        this.cpmDisplay = cpmDisplay;
        this.approvalPercentage = approvalPercentage;
        this.totalViewsDisplay = totalViewsDisplay;
        this.creatorsCount = creatorsCount;
    }

    public UUID getId() {
        return id;
    }

    public String getFunderProfilePic() {
        return funderProfilePic;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public String getPaidOutDisplay() {
        return paidOutDisplay;
    }

    public String getTotalBudgetDisplay() {
        return totalBudgetDisplay;
    }

    public String getCpmDisplay() {
        return cpmDisplay;
    }

    public String getApprovalPercentage() {
        return approvalPercentage;
    }

    public String getTotalViewsDisplay() {
        return totalViewsDisplay;
    }

    public long getCreatorsCount() {
        return creatorsCount;
    }
}