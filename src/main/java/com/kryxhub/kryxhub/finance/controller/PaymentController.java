package com.kryxhub.kryxhub.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.kryxhub.kryxhub.finance.service.StripePaymentService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "6. Finance & Payouts", description = "Stripe checkout, wallet funding, and creator transfers")
public class PaymentController {

    private final StripePaymentService stripePaymentService;

    public PaymentController(StripePaymentService stripePaymentService) {
        this.stripePaymentService = stripePaymentService;
    }

    @PostMapping("/checkout/campaign/{campaignId}")
    public ResponseEntity<?> createCampaignCheckout(@PathVariable UUID campaignId, Authentication authentication) {
        String funderEmail = authentication.getName();

        try {
            String checkoutUrl = stripePaymentService.createCheckoutSession(campaignId, funderEmail);
            
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "checkoutUrl", checkoutUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}