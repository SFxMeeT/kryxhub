package com.kryxhub.kryxhub.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class AdminCampaignDto {
    private UUID id;
    private String title;
    private String funderName;
    private String funderEmail;
    private String status;
    private BigDecimal totalBudget;
    private BigDecimal budgetRemaining;

    public AdminCampaignDto(UUID id, String title, String funderName, String funderEmail, String status, BigDecimal totalBudget, BigDecimal budgetRemaining) {
        this.id = id;
        this.title = title;
        this.funderName = funderName;
        this.funderEmail = funderEmail;
        this.status = status;
        this.totalBudget = totalBudget;
        this.budgetRemaining = budgetRemaining;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getFunderName() { return funderName; }
    public void setFunderName(String funderName) { this.funderName = funderName; }
    public String getFunderEmail() { return funderEmail; }
    public void setFunderEmail(String funderEmail) { this.funderEmail = funderEmail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
    public BigDecimal getBudgetRemaining() { return budgetRemaining; }
    public void setBudgetRemaining(BigDecimal budgetRemaining) { this.budgetRemaining = budgetRemaining; }
}