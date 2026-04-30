package com.kryxhub.kryxhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import com.kryxhub.kryxhub.dto.CreateCampaignRequest;
import com.kryxhub.kryxhub.service.CampaignService;
import com.kryxhub.kryxhub.entity.CampaignEntity;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/draft")
    public ResponseEntity<?> createDraftCampaign(
            @RequestBody CreateCampaignRequest request, 
            Authentication authentication) {
            
        String funderEmail = authentication.getName(); 

        try {
            CampaignEntity savedCampaign = campaignService.createDraftCampaign(request, funderEmail);
            
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Campaign drafted successfully",
                    "campaignId", savedCampaign.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}