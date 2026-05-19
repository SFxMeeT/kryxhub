package com.kryxhub.kryxhub.admin.controller;

import com.kryxhub.kryxhub.admin.dto.AdminCampaignDto;
import com.kryxhub.kryxhub.campaign.service.CampaignService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/campaigns")
@Tag(name = "8. Admin & Moderation", description = "Internal platform management tools for KryxHub staff")
public class AdminCampaignController {

    private final CampaignService campaignService;

    public AdminCampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminCampaignDto>> getAllCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<AdminCampaignDto> campaignFeed = campaignService.getAllCampaignsForAdmin(page, size);
        return ResponseEntity.ok(campaignFeed);
    }

    @PutMapping("/{campaignId}/force-close")
    public ResponseEntity<?> forceCloseCampaign(@PathVariable UUID campaignId) {
        try {
            String resultMessage = campaignService.forceCloseCampaign(campaignId);
            return ResponseEntity.ok(java.util.Map.of(
                    "status", "success",
                    "message", resultMessage
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{campaignId}/override-status")
    public ResponseEntity<?> overrideCampaignStatus(
            @PathVariable UUID campaignId, 
            @RequestParam String newStatus) {
        try {
            String resultMessage = campaignService.overrideCampaignStatus(campaignId, newStatus);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", resultMessage
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}