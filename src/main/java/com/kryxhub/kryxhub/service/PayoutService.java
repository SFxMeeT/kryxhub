package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.VideoStatsDto;
import com.kryxhub.kryxhub.entity.*;
import com.kryxhub.kryxhub.enums.PayoutStatus;
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
    private final StripeConnectService stripeConnectService;
    private final CampaignRepository campaignRepository;


    public PayoutService(SubmissionRepository submissionRepository, 
                         CampaignPlatformRepository platformRepository, 
                         ExternalApiRouter apiRouter,
                         PayoutRepository payoutRepository,
                         StripeConnectService stripeConnectService,
                         CampaignRepository campaignRepository) {
        this.submissionRepository = submissionRepository;
        this.platformRepository = platformRepository;
        this.apiRouter = apiRouter;
        this.payoutRepository = payoutRepository;
        this.stripeConnectService = stripeConnectService;
        this.campaignRepository = campaignRepository;
    }

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

                    executeCalculatedPayout(submission, newTotalViews, newlyEarnedAmount);
                }
            } catch (Exception e) {
                System.err.println("Failed to process payout for submission " + submission.getId() + ": " + e.getMessage());
            }
        }
    }

    @Transactional
    public void executeCalculatedPayout(SubmissionEntity submission, int newTotalViews, BigDecimal newlyEarnedAmount) {
        
        UserEntity creator = submission.getCreator();
        CampaignEntity campaign = submission.getCampaign();

        if (creator.getStripeAccountId() == null || creator.getStripeAccountId().isEmpty()) {
            System.out.println("Skipping payout: Creator " + creator.getUsername() + " has no linked bank.");
            return; 
        }

        if (campaign.getBudgetRemaining().compareTo(newlyEarnedAmount) < 0) {
            System.out.println("CRITICAL: Campaign out of budget for submission " + submission.getId());
            return; 
        }

        try {

            BigDecimal feePercentage = new BigDecimal("0.10"); // 10% fee
            BigDecimal platformFee = newlyEarnedAmount.multiply(feePercentage);
            BigDecimal netAmount = newlyEarnedAmount.subtract(platformFee);

            campaign.setBudgetRemaining(campaign.getBudgetRemaining().subtract(newlyEarnedAmount));
            campaignRepository.save(campaign);

            submission.setCurrentViews(newTotalViews);
            submission.setTotalEarned(submission.getTotalEarned().add(newlyEarnedAmount));
            submissionRepository.save(submission);

            String transferId = stripeConnectService.transferToCreator(creator.getStripeAccountId(), netAmount);

            PayoutEntity ledger = new PayoutEntity();
            ledger.setSubmission(submission);
            ledger.setCreator(creator);
            ledger.setAmountGross(newlyEarnedAmount);
            ledger.setPlatformFee(platformFee);
            ledger.setAmountNet(netAmount);
            ledger.setStripeTransferId(transferId);
            ledger.setStatus(PayoutStatus.PAID);
            
            payoutRepository.save(ledger);

            System.out.println("✅ Processed automated payout for " + submission.getVideoTitle() + "! Transfer ID: " + transferId);

        } catch (Exception e) {
            throw new RuntimeException("Stripe Transfer Failed, rolling back database: " + e.getMessage());
        }
    }
}