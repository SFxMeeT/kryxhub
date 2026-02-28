package com.kryxhub.kryxhub.entity;

import com.kryxhub.kryxhub.enums.LinkLabel;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "campaign_links", indexes = {
        @Index(name = "idx_campaign_links_campaign_id", columnList = "campaign_id")
})
public class CampaignLinksEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CampaignsEntity campaign;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private LinkLabel label;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public CampaignLinksEntity() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public CampaignsEntity getCampaign() {
        return campaign;
    }

    public void setCampaign(CampaignsEntity campaign) {
        this.campaign = campaign;
    }

    public LinkLabel getLabel() {
        return label;
    }

    public void setLabel(LinkLabel label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
