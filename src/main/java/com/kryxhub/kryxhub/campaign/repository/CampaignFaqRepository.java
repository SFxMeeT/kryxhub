package com.kryxhub.kryxhub.campaign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kryxhub.kryxhub.campaign.entity.CampaignFaqEntity;

import java.util.UUID;

@Repository
public interface CampaignFaqRepository extends JpaRepository<CampaignFaqEntity, UUID> {
}
