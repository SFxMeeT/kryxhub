package com.kryxhub.kryxhub.campaign.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "platform_settings")
public class PlatformSettingsEntity {

    @Id
    private Long id = 1L;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal platformFeeRate = new BigDecimal("0.10");

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getPlatformFeeRate() { return platformFeeRate; }
    public void setPlatformFeeRate(BigDecimal platformFeeRate) { this.platformFeeRate = platformFeeRate; }
}