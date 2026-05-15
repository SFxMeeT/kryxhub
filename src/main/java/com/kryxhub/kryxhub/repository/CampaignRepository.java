package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.CampaignEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
