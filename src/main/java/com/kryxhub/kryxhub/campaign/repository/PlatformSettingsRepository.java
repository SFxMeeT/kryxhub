package com.kryxhub.kryxhub.campaign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kryxhub.kryxhub.campaign.entity.PlatformSettingsEntity;

@Repository
public interface PlatformSettingsRepository extends JpaRepository<PlatformSettingsEntity, Long> {
}