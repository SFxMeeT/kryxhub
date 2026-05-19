package com.kryxhub.kryxhub.analytics.service;

import com.kryxhub.kryxhub.analytics.dto.FunderAnalyticsDto;
import com.kryxhub.kryxhub.campaign.entity.CampaignEntity;
import com.kryxhub.kryxhub.campaign.enums.CampaignType;
import com.kryxhub.kryxhub.submission.entity.SubmissionEntity;
import com.kryxhub.kryxhub.submission.enums.SubmissionStatus;
import com.kryxhub.kryxhub.submission.repository.SubmissionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class FunderAnalyticsService {

    private final SubmissionRepository submissionRepository;

    public FunderAnalyticsService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Transactional(readOnly = true)
    public FunderAnalyticsDto getFunderAnalytics(String funderEmail, String timeframe, CampaignType campaignType) {
        
        OffsetDateTime endDate = OffsetDateTime.now();
        OffsetDateTime startDate = timeframe.equalsIgnoreCase("6_MONTHS") 
                ? endDate.minusMonths(6) : endDate.minusDays(30);

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM d");
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
        String dateRangeDisplay = startDate.format(displayFormatter) + " - " + 
                                  endDate.format(displayFormatter) + ", " + endDate.format(yearFormatter);

        List<SubmissionEntity> submissions = submissionRepository.findFunderAnalyticsSubmissions(funderEmail, startDate, campaignType);

        long totalViews = 0;
        BigDecimal totalPayouts = BigDecimal.ZERO;
        int approvedCount = 0;
        
        Set<CampaignEntity> uniqueCampaigns = new HashSet<>();
        Map<String, FunderAnalyticsDto.ChartDataPoint> chartDataMap = new TreeMap<>();
        DateTimeFormatter chartDateFormatter = DateTimeFormatter.ofPattern("MMM d");

        for (SubmissionEntity sub : submissions) {
            totalViews += sub.getCurrentViews();
            totalPayouts = totalPayouts.add(sub.getTotalEarned());
            uniqueCampaigns.add(sub.getCampaign());
            
            if (sub.getStatus() == SubmissionStatus.APPROVED) {
                approvedCount++;
            }

            String dateKey = sub.getSubmittedAt().format(chartDateFormatter);
            chartDataMap.putIfAbsent(dateKey, new FunderAnalyticsDto.ChartDataPoint(dateKey, 0, 0, 0, 0));
            
            FunderAnalyticsDto.ChartDataPoint point = chartDataMap.get(dateKey);
            
            long likes = sub.getCurrentLikes() != null ? sub.getCurrentLikes() : 0;
            long comments = sub.getCurrentComments() != null ? sub.getCurrentComments() : 0;
            long shares = sub.getCurrentShares() != null ? sub.getCurrentShares() : 0;

            chartDataMap.put(dateKey, new FunderAnalyticsDto.ChartDataPoint(
                    dateKey, 
                    point.getViews() + sub.getCurrentViews(),
                    point.getLikes() + likes,
                    point.getComments() + comments,
                    point.getShares() + shares
            ));
        }

        BigDecimal avgOriginalCpm = BigDecimal.ZERO;
        if (!uniqueCampaigns.isEmpty()) {
            BigDecimal sumCpm = uniqueCampaigns.stream()
                    .flatMap(c -> c.getPlatforms().stream())
                    .map(p -> p.getCpmRate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            avgOriginalCpm = sumCpm.divide(new BigDecimal(uniqueCampaigns.size()), 2, RoundingMode.HALF_UP);
        }

        BigDecimal effectiveCpm = BigDecimal.ZERO;
        if (totalViews > 0) {
            effectiveCpm = totalPayouts.multiply(new BigDecimal(1000))
                    .divide(new BigDecimal(totalViews), 2, RoundingMode.HALF_UP);
        }

        String badge = effectiveCpm.compareTo(avgOriginalCpm) <= 0 && totalViews > 0 
                ? "Running Efficiently" : "Standard";

        int totalSubs = submissions.size();
        String approvalPercentage = totalSubs == 0 ? "0%" : ((approvedCount * 100) / totalSubs) + "%";

        FunderAnalyticsDto.Metrics metrics = new FunderAnalyticsDto.Metrics(
                totalViews, 
                "$" + String.format("%.2f", totalPayouts), 
                "$" + String.format("%.2f", effectiveCpm), 
                "$" + String.format("%.2f", avgOriginalCpm), 
                badge, 
                totalSubs, 
                approvedCount, 
                approvalPercentage
        );

        return new FunderAnalyticsDto(
                dateRangeDisplay, 
                metrics, 
                chartDataMap.values().stream().toList()
        );
    }
}