package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.VideoStatsDto;
import com.kryxhub.kryxhub.entity.*;
import com.kryxhub.kryxhub.enums.Platforms;
import com.kryxhub.kryxhub.enums.SubmissionStatus;
import com.kryxhub.kryxhub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PayoutService {

    private final SubmissionRepository submissionRepository;
    private final CampaignPlatformRepository platformRepository;
    private final ExternalApiRouter apiRouter;
    private final PayoutRepository payoutRepository; 

    public PayoutService(SubmissionRepository submissionRepository, 
                         CampaignPlatformRepository platformRepository, 
                         ExternalApiRouter apiRouter,
                         PayoutRepository payoutRepository) {
        this.submissionRepository = submissionRepository;
        this.platformRepository = platformRepository;
        this.apiRouter = apiRouter;
        this.payoutRepository = payoutRepository;
    }

    @Transactional
    public void processAutomatedPayouts() {

        List<SubmissionEntity> activeSubmissions = submissionRepository.findByStatus(SubmissionStatus.APPROVED);

        for (SubmissionEntity submission : activeSubmissions) {
            try {

                VideoStatsDto latestStats = apiRouter.getStats(submission.getPlatformName(), submission.getVideoUrl());
                Integer newTotalViews = latestStats.getViewCount();

                Integer previousViews = submission.getCurrentViews();
                int newViewsToPayFor = newTotalViews - previousViews;

                if (newViewsToPayFor > 0) {
                    CampaignPlatformEntity platformRules = platformRepository
                            .findByCampaignIdAndPlatformName(submission.getCampaign().getId(), submission.getPlatformName())
                            .orElseThrow(() -> new RuntimeException("Platform rules not found"));

                    BigDecimal viewsInThousands = BigDecimal.valueOf(newViewsToPayFor).divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
                    BigDecimal newlyEarnedAmount = viewsInThousands.multiply(platformRules.getCpmRate());

                    submission.setCurrentViews(newTotalViews);
                    submission.setTotalEarned(submission.getTotalEarned().add(newlyEarnedAmount));
                    submissionRepository.save(submission);

                    PayoutEntity payoutLedger = new PayoutEntity();
                    payoutLedger.setSubmission(submission);
                    payoutLedger.setCreator(submission.getCreator());
                    
                    payoutLedger.setAmountGross(newlyEarnedAmount);
                    payoutLedger.setPlatformFee(BigDecimal.ZERO);
                    payoutLedger.setAmountNet(newlyEarnedAmount);
                    
                    payoutRepository.save(payoutLedger);
                    
                    System.out.println("💰 Processed payout for " + submission.getVideoTitle() + ": $" + newlyEarnedAmount);
                }
            } catch (Exception e) {
                System.err.println("Failed to process payout for submission " + submission.getId() + ": " + e.getMessage());
            }
        }
    }
}