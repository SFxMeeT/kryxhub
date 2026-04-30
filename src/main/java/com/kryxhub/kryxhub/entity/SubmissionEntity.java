package com.kryxhub.kryxhub.entity;

import com.kryxhub.kryxhub.enums.Platforms;
import com.kryxhub.kryxhub.enums.SubmissionStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "submissions", indexes = {
        @Index(name = "idx_submissions_campaign_id", columnList = "campaign_id"),
        @Index(name = "idx_submissions_creator_id", columnList = "creator_id"),
        @Index(name = "idx_submissions_status", columnList = "status")
})
public class SubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private UserEntity creator;

    @Column(name = "video_title", nullable = false, length = 255)
    private String videoTitle;

    @Column(name = "video_url", nullable = false, columnDefinition = "TEXT")
    private String videoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_name", nullable = false, length = 50)
    private Platforms platformName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @Column(name = "current_views", nullable = false)
    private Integer currentViews = 0;

    @Column(name = "estimated_payout", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedPayout = BigDecimal.ZERO;

    @Column(name = "total_earned", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalEarned = BigDecimal.ZERO;

    @Column(name = "funder_notes", columnDefinition = "TEXT")
    private String funderNotes;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private OffsetDateTime submittedAt;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionAnswerEntity> answers = new ArrayList<>();

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    private List<PayoutEntity> payouts = new ArrayList<>();

    public SubmissionEntity() {
    }

    @PrePersist
    protected void onCreate() {
        this.submittedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public CampaignEntity getCampaign() {
        return campaign;
    }

    public void setCampaign(CampaignEntity campaign) {
        this.campaign = campaign;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Platforms getPlatformName() {
        return platformName;
    }

    public void setPlatformName(Platforms platformName) {
        this.platformName = platformName;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public Integer getCurrentViews() {
        return currentViews;
    }

    public void setCurrentViews(Integer currentViews) {
        this.currentViews = currentViews;
    }

    public BigDecimal getEstimatedPayout() {
        return estimatedPayout;
    }

    public void setEstimatedPayout(BigDecimal estimatedPayout) {
        this.estimatedPayout = estimatedPayout;
    }

    public BigDecimal getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(BigDecimal totalEarned) {
        this.totalEarned = totalEarned;
    }

    public String getFunderNotes() {
        return funderNotes;
    }

    public void setFunderNotes(String funderNotes) {
        this.funderNotes = funderNotes;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public OffsetDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(OffsetDateTime reviewedAt) {
        this.reviewedAt = reviewedAt
    }

    public List<SubmissionAnswerEntity> getAnswers() {
        return answers;
    }

    public List<PayoutEntity> getPayouts() {
        return payouts;
    }
}
