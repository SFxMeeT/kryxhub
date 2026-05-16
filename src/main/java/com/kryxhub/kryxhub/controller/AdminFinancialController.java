package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.AdminRevenueDto;
import com.kryxhub.kryxhub.service.PayoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kryxhub.kryxhub.dto.AdminPayoutDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/admin/finance")
public class AdminFinancialController {

    private final PayoutService payoutService;

    public AdminFinancialController(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<AdminRevenueDto> getPlatformRevenue() {
        try {
            AdminRevenueDto overview = payoutService.getPlatformFinancialOverview();
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/payouts")
    public ResponseEntity<Page<AdminPayoutDto>> getMasterLedger(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Page<AdminPayoutDto> ledgerFeed = payoutService.getMasterLedger(page, size);
            return ResponseEntity.ok(ledgerFeed);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/retry-transfer/{payoutId}")
    public ResponseEntity<?> retryFailedTransfer(@PathVariable java.util.UUID payoutId) {
        try {
            String resultMessage = payoutService.retryFailedTransfer(payoutId);
            return ResponseEntity.ok(java.util.Map.of(
                    "status", "success",
                    "message", resultMessage
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}