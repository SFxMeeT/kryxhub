package com.kryxhub.kryxhub.campaign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kryxhub.kryxhub.campaign.entity.CampaignQuestionEntity;

import java.util.UUID;

@Repository
public interface CampaignQuestionRepository extends JpaRepository<CampaignQuestionEntity, UUID> {
}
