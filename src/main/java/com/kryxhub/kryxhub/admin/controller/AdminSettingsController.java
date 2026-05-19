package com.kryxhub.kryxhub.admin.controller;

import com.kryxhub.kryxhub.campaign.entity.PlatformSettingsEntity;
import com.kryxhub.kryxhub.campaign.repository.PlatformSettingsRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin/settings")
@Tag(name = "8. Admin & Moderation", description = "Internal platform management tools for KryxHub staff")
public class AdminSettingsController {

    private final PlatformSettingsRepository settingsRepository;

    public AdminSettingsController(PlatformSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @GetMapping
    public ResponseEntity<PlatformSettingsEntity> getSettings() {
        PlatformSettingsEntity settings = settingsRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Settings missing"));
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/fee")
    public ResponseEntity<?> updatePlatformFee(@RequestParam BigDecimal newFeeRate) {
        
        if (newFeeRate.compareTo(BigDecimal.ZERO) < 0 || newFeeRate.compareTo(BigDecimal.ONE) > 0) {
            return ResponseEntity.badRequest().body("Fee rate must be between 0.00 and 1.00");
        }

        PlatformSettingsEntity settings = settingsRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Settings missing"));

        settings.setPlatformFeeRate(newFeeRate);
        settingsRepository.save(settings);

        return ResponseEntity.ok(java.util.Map.of(
                "status", "success",
                "message", "Global platform fee successfully updated to " + (newFeeRate.multiply(new BigDecimal("100"))) + "%"
        ));
    }
}