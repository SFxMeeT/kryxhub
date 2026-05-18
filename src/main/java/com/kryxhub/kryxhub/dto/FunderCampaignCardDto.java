package com.kryxhub.kryxhub.dto;

import java.util.List;
import java.util.UUID;

public class FunderCampaignCardDto {
    private UUID campaignId;
    private String title;
    private String statusDisplay;
    private List<String> tags;
    private String paidOutDisplay;
    private int budgetSpentPercentage;
    private String cpmDisplay;
    private long viewsCount;
    private int submissionsCount;

    private boolean canArchive;
    private boolean canEdit;
    private boolean canPause;
    private boolean canFund;

    public FunderCampaignCardDto(UUID campaignId, String title, String statusDisplay, List<String> tags, 
                                 String paidOutDisplay, int budgetSpentPercentage, String cpmDisplay, 
                                 long viewsCount, int submissionsCount, boolean canArchive, 
                                 boolean canEdit, boolean canPause, boolean canFund) {
        this.campaignId = campaignId;
        this.title = title;
        this.statusDisplay = statusDisplay;
        this.tags = tags;
        this.paidOutDisplay = paidOutDisplay;
        this.budgetSpentPercentage = budgetSpentPercentage;
        this.cpmDisplay = cpmDisplay;
        this.viewsCount = viewsCount;
        this.submissionsCount = submissionsCount;
        this.canArchive = canArchive;
        this.canEdit = canEdit;
        this.canPause = canPause;
        this.canFund = canFund;
    }

    public UUID getCampaignId() { return campaignId; }
    public String getTitle() { return title; }
    public String getStatusDisplay() { return statusDisplay; }
    public List<String> getTags() { return tags; }
    public String getPaidOutDisplay() { return paidOutDisplay; }
    public int getBudgetSpentPercentage() { return budgetSpentPercentage; }
    public String getCpmDisplay() { return cpmDisplay; }
    public long getViewsCount() { return viewsCount; }
    public int getSubmissionsCount() { return submissionsCount; }
    public boolean isCanArchive() { return canArchive; }
    public boolean isCanEdit() { return canEdit; }
    public boolean isCanPause() { return canPause; }
    public boolean isCanFund() { return canFund; }
}