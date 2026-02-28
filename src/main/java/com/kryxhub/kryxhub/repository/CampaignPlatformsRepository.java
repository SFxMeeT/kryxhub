package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.CampaignPlatformsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CampaignPlatformsRepository extends JpaRepository<CampaignPlatformsEntity, UUID> {
}
