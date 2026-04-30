package com.kryxhub.kryxhub.scheduler; 

import com.kryxhub.kryxhub.service.PayoutService; 

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PayoutScheduler {

    private final PayoutService payoutService;

    public PayoutScheduler(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @Scheduled(cron = "0 0 */6 * * *") 
    public void runPayoutEngine() {
        System.out.println("Waking up Payout Engine to process creator earnings...");
        payoutService.processAutomatedPayouts();
        System.out.println("Payout Engine finished.");
    }
}