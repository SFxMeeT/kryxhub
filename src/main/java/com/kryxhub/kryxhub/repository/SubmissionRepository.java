package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.SubmissionEntity;
import com.kryxhub.kryxhub.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, UUID> {

    boolean existsByVideoUrl(String videoUrl);

    List<SubmissionEntity> findByCampaignId(UUID campaignId);

    List<SubmissionEntity> findByStatus(SubmissionStatus status);
    
}