package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.CampaignEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.enums.CampaignCategory;
import com.kryxhub.kryxhub.enums.CampaignType;
import com.kryxhub.kryxhub.enums.Platforms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity, UUID> {

    Page<CampaignEntity> findByStatusAndBudgetRemainingGreaterThan(String status, BigDecimal budget, Pageable pageable);
    
    List<CampaignEntity> findByFunder(UserEntity funder);

    @Query(
        "SELECT DISTINCT c FROM CampaignEntity c " +
        "LEFT JOIN c.platforms p " +
        "WHERE c.status = 'ACTIVE' AND c.budgetRemaining > 0 " +
        "AND c.showOnDiscover = true " +
        "AND (:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS String), '%'))) " +
        "AND (:category IS NULL OR c.category = :category) " +
        "AND (:type IS NULL OR c.type = :type) " +
        "AND (:platform IS NULL OR p.platformName = :platform)"
    )
    Page<CampaignEntity> discoverCampaignsWithFilters(
            @Param("keyword") String keyword,
            @Param("category") CampaignCategory category,
            @Param("type") CampaignType type,
            @Param("platform") Platforms platform,
            Pageable pageable
    );

    @Query(
        "SELECT c FROM CampaignEntity c " +
        "WHERE c.funder.email = :funderEmail " +
        "AND (:status IS NULL OR c.status = :status)"
    )
    Page<CampaignEntity> findFunderCampaigns(
            @Param("funderEmail") String funderEmail,
            @Param("status") String status, 
            Pageable pageable
    );
}
