package com.kryxhub.kryxhub.entity;

import com.kryxhub.kryxhub.enums.CampaignCategory;
import com.kryxhub.kryxhub.enums.CampaignType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "campaigns", indexes = {
        @Index(name = "idx_campaigns_funder_id", columnList = "funder_id"),
        @Index(name = "idx_campaigns_status", columnList = "status"),
        @Index(name = "idx_campaigns_category", columnList = "category")
})
public class CampaignsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funder_id", nullable = false)
    private UsersEntity funder;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CampaignType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CampaignCategory category;

    @Column(name = "thumbnail_url", nullable = false, columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "total_budget", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalBudget;

    @Column(name = "budget_remaining", nullable = false, precision = 10, scale = 2)
    private BigDecimal budgetRemaining;

    @Column(name = "requires_application")
    private Boolean requiresApplication = false;

    @Column(name = "show_on_discover")
    private Boolean showOnDiscover = true;

    @Column(nullable = false, length = 20)
    private String status = "DRAFT";

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "funded_at")
    private OffsetDateTime fundedAt;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignPlatformsEntity> platforms = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignRulesEntity> rules = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignLinksEntity> links = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignFaqsEntity> faqs = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignQuestionsEntity> questions = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<SubmissionsEntity> submissions = new ArrayList<>();

    public CampaignsEntity() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UsersEntity getFunder() {
        return funder;
    }

    public void setFunder(UsersEntity funder) {
        this.funder = funder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CampaignType getType() {
        return type;
    }

    public void setType(CampaignType type) {
        this.type = type;
    }

    public CampaignCategory getCategory() {
        return category;
    }

    public void setCategory(CampaignCategory category) {
        this.category = category;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getBudgetRemaining() {
        return budgetRemaining;
    }

    public void setBudgetRemaining(BigDecimal budgetRemaining) {
        this.budgetRemaining = budgetRemaining;
    }

    public Boolean getRequiresApplication() {
        return requiresApplication;
    }

    public void setRequiresApplication(Boolean requiresApplication) {
        this.requiresApplication = requiresApplication;
    }

    public Boolean getShowOnDiscover() {
        return showOnDiscover;
    }

    public void setShowOnDiscover(Boolean showOnDiscover) {
        this.showOnDiscover = showOnDiscover;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getFundedAt() {
        return fundedAt;
    }

    public List<CampaignPlatformsEntity> getPlatforms() {
        return platforms;
    }

    public List<CampaignRulesEntity> getRules() {
        return rules;
    }

    public List<CampaignLinksEntity> getLinks() {
        return links;
    }

    public List<CampaignFaqsEntity> getFaqs() {
        return faqs;
    }

    public List<CampaignQuestionsEntity> getQuestions() {
        return questions;
    }

    public List<SubmissionsEntity> getSubmissions() {
        return submissions;
    }

}
