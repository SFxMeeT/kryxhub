package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.CreatorAnalyticsDto;
import com.kryxhub.kryxhub.entity.SubmissionEntity;
import com.kryxhub.kryxhub.entity.LinkedSocialAccountEntity;
import com.kryxhub.kryxhub.enums.CampaignType;
import com.kryxhub.kryxhub.enums.SubmissionStatus;
import com.kryxhub.kryxhub.repository.SubmissionRepository;
import com.kryxhub.kryxhub.repository.LinkedSocialAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final SubmissionRepository submissionRepository;
    private final LinkedSocialAccountRepository linkedAccountRepository;

    public AnalyticsService(SubmissionRepository submissionRepository, LinkedSocialAccountRepository linkedAccountRepository) {
        this.submissionRepository = submissionRepository;
        this.linkedAccountRepository = linkedAccountRepository;
    }

    @Transactional(readOnly = true)
    public CreatorAnalyticsDto getCreatorAnalytics(String creatorEmail, String timeframe, CampaignType campaignType) {

        OffsetDateTime endDate = OffsetDateTime.now();
        OffsetDateTime startDate = timeframe.equalsIgnoreCase("6_MONTHS") 
                ? endDate.minusMonths(6) : endDate.minusDays(30);

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM d");
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
        String dateRangeDisplay = startDate.format(displayFormatter) + " - " + 
                                  endDate.format(displayFormatter) + ", " + endDate.format(yearFormatter);

        List<String> linkedPlatforms = linkedAccountRepository.findByUserEmail(creatorEmail).stream()
                .filter(LinkedSocialAccountEntity::getVerified)
                .map(acc -> acc.getPlatform().name())
                .distinct()
                .collect(Collectors.toList());

        List<SubmissionEntity> submissions = submissionRepository.findAnalyticsSubmissions(creatorEmail, startDate, campaignType);

        long totalViews = 0;
        BigDecimal totalPayouts = BigDecimal.ZERO;
        BigDecimal activeCampaignsEarning = BigDecimal.ZERO;
        int approvedCount = 0;

        Map<String, CreatorAnalyticsDto.ChartDataPoint> chartDataMap = new TreeMap<>();
        DateTimeFormatter chartDateFormatter = DateTimeFormatter.ofPattern("MMM d");

        for (SubmissionEntity sub : submissions) {
            totalViews += sub.getCurrentViews();
            totalPayouts = totalPayouts.add(sub.getTotalEarned());
            
            if (sub.getStatus() == SubmissionStatus.APPROVED) {
                approvedCount++;
            }

            if ("ACTIVE".equals(sub.getCampaign().getStatus())) {
                activeCampaignsEarning = activeCampaignsEarning.add(sub.getTotalEarned());
            }

            String dateKey = sub.getSubmittedAt().format(chartDateFormatter);
            chartDataMap.putIfAbsent(dateKey, new CreatorAnalyticsDto.ChartDataPoint(dateKey, 0, BigDecimal.ZERO));
            
            CreatorAnalyticsDto.ChartDataPoint point = chartDataMap.get(dateKey);
            point.views += sub.getCurrentViews();
            point.payouts = point.payouts.add(sub.getTotalEarned());
        }

        int totalSubs = submissions.size();
        String approvalPercentage = totalSubs == 0 ? "0%" : ((approvedCount * 100) / totalSubs) + "%";

        CreatorAnalyticsDto.Metrics metrics = new CreatorAnalyticsDto.Metrics(
                totalViews, 
                "$" + String.format("%.2f", totalPayouts), 
                totalSubs, 
                approvedCount, 
                approvalPercentage, 
                "$" + String.format("%.2f", activeCampaignsEarning)
        );

        return new CreatorAnalyticsDto(
                dateRangeDisplay, 
                linkedPlatforms, 
                metrics, 
                chartDataMap.values().stream().toList()
        );
    }
}