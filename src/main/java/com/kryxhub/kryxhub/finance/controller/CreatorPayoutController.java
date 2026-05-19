package com.kryxhub.kryxhub.finance.controller;

import com.kryxhub.kryxhub.finance.dto.StripeLinkResponse;
import com.kryxhub.kryxhub.finance.service.StripeConnectService;
import com.kryxhub.kryxhub.user.entity.UserEntity;
import com.kryxhub.kryxhub.user.repository.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/payouts")
@Tag(name = "6. Finance & Payouts", description = "Stripe checkout, wallet funding, and creator transfers")
public class CreatorPayoutController {

    private final StripeConnectService stripeConnectService;
    private final UserRepository userRepository;

    public CreatorPayoutController(StripeConnectService stripeConnectService, UserRepository userRepository) {
        this.stripeConnectService = stripeConnectService;
        this.userRepository = userRepository;
    }

    @PostMapping("/onboard")
    public ResponseEntity<?> onboardCreator(Authentication authentication) {
        try {
            UserEntity creator = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String stripeAccountId = stripeConnectService.createConnectAccount(creator);
            String onboardingUrl = stripeConnectService.generateOnboardingLink(stripeAccountId);

            return ResponseEntity.ok(new StripeLinkResponse(onboardingUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/onboard/refresh")
    public ResponseEntity<?> refreshOnboarding() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Link expired. Please go back to your dashboard and click 'Link Bank Account' again.");
    }

    @GetMapping("/onboard/return")
    public ResponseEntity<Void> returnFromOnboarding() {

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("https://kryxhub-api.onrender.com/onboard-success.html"))
                .build();
    }

    @PostMapping("/test-transfer")
    public ResponseEntity<?> testManualTransfer(Authentication authentication, @RequestParam String amount) {
        try {

            UserEntity creator = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (creator.getStripeAccountId() == null || creator.getStripeAccountId().isEmpty()) {
                return ResponseEntity.badRequest().body("Creator has not linked a bank account yet!");
            }

            BigDecimal payoutAmount = new BigDecimal(amount);

            String transferId = stripeConnectService.transferToCreator(creator.getStripeAccountId(), payoutAmount);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "$" + amount + " transferred to creator!",
                    "stripeTransferId", transferId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}