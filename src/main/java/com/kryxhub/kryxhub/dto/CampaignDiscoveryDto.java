package com.kryxhub.kryxhub.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CampaignDiscoveryDto {
    private UUID id;
    private String title;
    private String description;
    private String funderName;
    private BigDecimal budgetRemaining;

    public CampaignDiscoveryDto() {}

    public CampaignDiscoveryDto(UUID id, String title, String description, String funderName, BigDecimal budgetRemaining) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.funderName = funderName;
        this.budgetRemaining = budgetRemaining;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFunderName() { return funderName; }
    public void setFunderName(String funderName) { this.funderName = funderName; }
    public BigDecimal getBudgetRemaining() { return budgetRemaining; }
    public void setBudgetRemaining(BigDecimal budgetRemaining) { this.budgetRemaining = budgetRemaining; }
}