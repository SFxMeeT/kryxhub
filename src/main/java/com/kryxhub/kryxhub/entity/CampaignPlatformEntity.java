package com.kryxhub.kryxhub.entity;

import com.kryxhub.kryxhub.enums.Platforms;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "campaign_platforms", indexes = {
        @Index(name = "idx_campaign_platforms_campaign_id", columnList = "campaign_id")
})
public class CampaignPlatformEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CampaignEntity campaign;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_name", nullable = false, length = 50)
    private Platforms platformName;

    @Column(name = "cpm_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal cpmRate;

    @Column(name = "min_payout", nullable = false, precision = 10, scale = 2)
    private BigDecimal minPayout = BigDecimal.ZERO;

    @Column(name = "max_payout", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxPayout;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public CampaignPlatformEntity() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
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

    public Platforms getPlatformName() {
        return platformName;
    }

    public void setPlatformName(Platforms platformName) {
        this.platformName = platformName;
    }

    public BigDecimal getCpmRate() {
        return cpmRate;
    }

    public void setCpmRate(BigDecimal cpmRate) {
        this.cpmRate = cpmRate;
    }

    public BigDecimal getMinPayout() {
        return minPayout;
    }

    public void setMinPayout(BigDecimal minPayout) {
        this.minPayout = minPayout;
    }

    public BigDecimal getMaxPayout() {
        return maxPayout;
    }

    public void setMaxPayout(BigDecimal maxPayout) {
        this.maxPayout = maxPayout;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}