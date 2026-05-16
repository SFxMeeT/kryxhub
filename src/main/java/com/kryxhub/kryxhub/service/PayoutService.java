package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.VideoStatsDto;
import com.kryxhub.kryxhub.dto.AdminRevenueDto;
import com.kryxhub.kryxhub.dto.AdminPayoutDto;
import com.kryxhub.kryxhub.entity.*;
import com.kryxhub.kryxhub.enums.PayoutStatus;
import com.kryxhub.kryxhub.enums.SubmissionStatus;
import com.kryxhub.kryxhub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    private final PlatformSettingsRepository settingsRepository;

    public PayoutService(SubmissionRepository submissionRepository, 
                         CampaignPlatformRepository platformRepository, 
                         ExternalApiRouter apiRouter,
                         PayoutRepository payoutRepository,
                         StripeConnectService stripeConnectService,
                         CampaignRepository campaignRepository,
                         PlatformSettingsRepository settingsRepository) {
        this.submissionRepository = submissionRepository;
        this.platformRepository = platformRepository;
        this.apiRouter = apiRouter;
        this.payoutRepository = payoutRepository;
        this.stripeConnectService = stripeConnectService;
        this.campaignRepository = campaignRepository;
        this.settingsRepository = settingsRepository;
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

        if (!"ACTIVE".equals(campaign.getStatus())) {
            System.out.println("Skipping payout: Campaign " + campaign.getId() + " is " + campaign.getStatus());
            return;
        }

        try {

            PlatformSettingsEntity settings = settingsRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Platform settings not found!"));

            BigDecimal platformFee = newlyEarnedAmount.multiply(settings.getPlatformFeeRate());

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

    public AdminRevenueDto getPlatformFinancialOverview() {

        PayoutStatus targetStatus = PayoutStatus.PAID; 

        BigDecimal revenue = payoutRepository.sumPlatformRevenueByStatus(targetStatus);
        BigDecimal processed = payoutRepository.sumGrossProcessedByStatus(targetStatus);
        long count = payoutRepository.countByStatus(targetStatus);

        if (revenue == null) revenue = BigDecimal.ZERO;
        if (processed == null) processed = BigDecimal.ZERO;

        return new AdminRevenueDto(revenue, processed, count);
    }

    public Page<AdminPayoutDto> getMasterLedger(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<PayoutEntity> payouts = payoutRepository.findAll(pageable);

        return payouts.map(payout -> new AdminPayoutDto(
                payout.getId(),
                payout.getCreator().getUsername(),
                payout.getSubmission().getCampaign().getTitle(),
                payout.getAmountGross(),
                payout.getPlatformFee(),
                payout.getAmountNet(),
                payout.getStripeTransferId(),
                payout.getStatus().name()
        ));
    }

    @Transactional
    public String retryFailedTransfer(java.util.UUID payoutId) {

        PayoutEntity payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout record not found."));

        if (payout.getStatus() == PayoutStatus.PAID) {
            throw new RuntimeException("CRITICAL: This payout is already marked as PAID. Retry aborted.");
        }

        UserEntity creator = payout.getCreator();

        if (creator.getStripeAccountId() == null || creator.getStripeAccountId().isEmpty()) {
            throw new RuntimeException("Retry aborted: Creator does not have a linked Stripe account.");
        }

        try {

            String newTransferId = stripeConnectService.transferToCreator(
                    creator.getStripeAccountId(), 
                    payout.getAmountNet()
            );

            payout.setStripeTransferId(newTransferId);
            payout.setStatus(PayoutStatus.PAID);
            payoutRepository.save(payout);

            return "Retry successful! Money transferred. New Receipt ID: " + newTransferId;

        } catch (Exception e) {

            payout.setStatus(PayoutStatus.FAILED);
            payoutRepository.save(payout);
            
            throw new RuntimeException("Stripe rejected the retry: " + e.getMessage());
        }
    }
}