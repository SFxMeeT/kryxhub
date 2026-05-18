package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.SubmissionEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.enums.CampaignType;
import com.kryxhub.kryxhub.enums.SubmissionStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, UUID> {

    boolean existsByVideoUrl(String videoUrl);

    List<SubmissionEntity> findByCampaignId(UUID campaignId);

    List<SubmissionEntity> findByStatus(SubmissionStatus status);

    List<SubmissionEntity> findByCreator(UserEntity creator);

    @Query(
        "SELECT s FROM SubmissionEntity s " +
        "WHERE s.creator.email = :creatorEmail " +
        "AND (:status IS NULL OR s.status = :status) " +
        "AND (:campaignId IS NULL OR s.campaign.id = :campaignId) " +
        "AND (:campaignType IS NULL OR s.campaign.type = :campaignType)"
    )
    Page<SubmissionEntity> findCreatorSubmissionsWithFilters(
            @Param("creatorEmail") String creatorEmail,
            @Param("status") SubmissionStatus status,
            @Param("campaignId") UUID campaignId,
            @Param("campaignType") CampaignType campaignType,
            Pageable pageable
    );

    @Query("SELECT s FROM SubmissionEntity s " +
           "WHERE s.creator.email = :creatorEmail " +
           "AND s.submittedAt >= :startDate " +
           "AND (:campaignType IS NULL OR s.campaign.type = :campaignType)")
    List<SubmissionEntity> findAnalyticsSubmissions(
            @Param("creatorEmail") String creatorEmail,
            @Param("startDate") OffsetDateTime startDate,
            @Param("campaignType") CampaignType campaignType
    );

    @Query(
        "SELECT s FROM SubmissionEntity s " +
        "WHERE s.campaign.funder.email = :funderEmail " +
        "AND s.submittedAt >= :startDate"
    )
    List<SubmissionEntity> findFunderMetricsSubmissions(
            @Param("funderEmail") String funderEmail,
            @Param("startDate") OffsetDateTime startDate
    );

    @Query("SELECT s FROM SubmissionEntity s " +
           "WHERE s.campaign.funder.email = :funderEmail " +
           "AND s.submittedAt >= :startDate " +
           "AND (:campaignType IS NULL OR s.campaign.type = :campaignType)")
    List<SubmissionEntity> findFunderAnalyticsSubmissions(
            @Param("funderEmail") String funderEmail,
            @Param("startDate") OffsetDateTime startDate,
            @Param("campaignType") CampaignType campaignType
    );
    
}