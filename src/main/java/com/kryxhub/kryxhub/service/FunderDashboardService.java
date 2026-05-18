package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.FunderCampaignCardDto;
import com.kryxhub.kryxhub.dto.FunderMetricsDto;
import com.kryxhub.kryxhub.entity.CampaignEntity;
import com.kryxhub.kryxhub.entity.SubmissionEntity;
import com.kryxhub.kryxhub.enums.SubmissionStatus;
import com.kryxhub.kryxhub.repository.CampaignRepository;
import com.kryxhub.kryxhub.repository.SubmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FunderDashboardService {

    private final SubmissionRepository submissionRepository;
    private final CampaignRepository campaignRepository;

    public FunderDashboardService(SubmissionRepository submissionRepository, CampaignRepository campaignRepository) {
        this.submissionRepository = submissionRepository;
        this.campaignRepository = campaignRepository;
    }

    @Transactional(readOnly = true)
    public FunderMetricsDto getFunderMetrics(String funderEmail, String timeframe) {

        OffsetDateTime startDate = timeframe.equalsIgnoreCase("24H") 
                ? OffsetDateTime.now().minusHours(24) 
                : OffsetDateTime.now().minusDays(7);

        List<SubmissionEntity> submissions = submissionRepository.findFunderMetricsSubmissions(funderEmail, startDate);

        int pendingSubs = 0;
        int approvedSubs = 0;
        long totalViews = 0;
        BigDecimal paidOut = BigDecimal.ZERO;

        for (SubmissionEntity sub : submissions) {
            totalViews += sub.getCurrentViews();
            paidOut = paidOut.add(sub.getTotalEarned());
            
            if (sub.getStatus() == SubmissionStatus.PENDING) pendingSubs++;
            if (sub.getStatus() == SubmissionStatus.APPROVED) approvedSubs++;
        }

        int totalSubs = submissions.size();
        String approvalRate = totalSubs == 0 ? "0%" : ((approvedSubs * 100) / totalSubs) + "%";

        return new FunderMetricsDto(
                pendingSubs,
                totalViews,
                "$" + String.format("%.2f", paidOut),
                approvalRate
        );
    }

    @Transactional(readOnly = true)
    public Page<FunderCampaignCardDto> getFunderCampaigns(String funderEmail, String tabFilter, int page, int size) {
        
        String dbStatus = switch (tabFilter != null ? tabFilter.toUpperCase() : "ALL") {
            case "ACTIVE" -> "ACTIVE";
            case "PENDING_BUDGET" -> "DRAFT";
            case "ENDED" -> "ENDED";
            default -> null;
        };

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CampaignEntity> campaigns = campaignRepository.findFunderCampaigns(funderEmail, dbStatus, pageable);

        return campaigns.map(c -> {
            
            List<String> tags = new ArrayList<>();
            if (c.getType() != null) tags.add(c.getType().name());
            if (c.getCategory() != null) tags.add(c.getCategory().name());
            c.getPlatforms().forEach(p -> tags.add(p.getPlatformName().name()));

            BigDecimal totalBudget = c.getTotalBudget() != null ? c.getTotalBudget() : BigDecimal.ZERO;
            BigDecimal budgetRemaining = c.getBudgetRemaining() != null ? c.getBudgetRemaining() : BigDecimal.ZERO;
            BigDecimal spent = totalBudget.subtract(budgetRemaining);
            
            int spentPercentage = totalBudget.compareTo(BigDecimal.ZERO) == 0 ? 0 
                    : spent.multiply(new BigDecimal(100)).divide(totalBudget, RoundingMode.HALF_UP).intValue();
            
            String paidOutDisplay = "$" + spent.intValue() + " / $" + totalBudget.intValue();

            String cpmDisplay = c.getPlatforms().isEmpty() ? "$0.00 / 1k views" 
                    : "$" + c.getPlatforms().get(0).getCpmRate() + " / 1k views";

            String currentStatus = c.getStatus() != null ? c.getStatus() : "DRAFT";
            boolean isDraft = currentStatus.equals("DRAFT");
            boolean isActive = currentStatus.equals("ACTIVE");
            
            boolean canEdit = isDraft;
            boolean canFund = isDraft || (isActive && budgetRemaining.compareTo(BigDecimal.ZERO) == 0);
            boolean canPause = isActive;
            boolean canArchive = isActive || currentStatus.equals("PAUSED") || currentStatus.equals("ENDED");

            return new FunderCampaignCardDto(
                    c.getId(),
                    c.getTitle(),
                    currentStatus,
                    tags,
                    paidOutDisplay,
                    spentPercentage,
                    cpmDisplay,
                    c.getViewCount() != null ? c.getViewCount() : 0,
                    c.getSubmissions() != null ? c.getSubmissions().size() : 0,
                    canArchive, canEdit, canPause, canFund
            );
        });
    }
}