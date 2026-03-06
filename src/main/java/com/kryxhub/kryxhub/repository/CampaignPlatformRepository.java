package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.CampaignPlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CampaignPlatformRepository extends JpaRepository<CampaignPlatformEntity, UUID> {
}
