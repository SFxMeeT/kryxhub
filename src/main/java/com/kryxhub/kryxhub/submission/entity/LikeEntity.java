package com.kryxhub.kryxhub.submission.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.kryxhub.kryxhub.campaign.entity.CampaignEntity;
import com.kryxhub.kryxhub.user.entity.UserEntity;

@Entity
@Table(name = "campaign_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "campaign_id"}) 
})
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public CampaignEntity getCampaign() { return campaign; }
    public void setCampaign(CampaignEntity campaign) { this.campaign = campaign; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}