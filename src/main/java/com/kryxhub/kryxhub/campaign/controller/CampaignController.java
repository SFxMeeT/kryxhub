package com.kryxhub.kryxhub.campaign.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import com.kryxhub.kryxhub.analytics.dto.OverviewFeedDto;
import com.kryxhub.kryxhub.campaign.dto.CreateCampaignRequest;
import com.kryxhub.kryxhub.campaign.entity.CampaignEntity;
import com.kryxhub.kryxhub.campaign.service.CampaignService;

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

    @PostMapping(value = "/{id}/thumbnail", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCampaignThumbnail(
            @PathVariable java.util.UUID id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            Authentication authentication) {
        try {
            String imageUrl = campaignService.updateCampaignThumbnail(id, authentication.getName(), file);
            return ResponseEntity.ok(java.util.Map.of(
                    "message", "Thumbnail updated successfully!",
                    "url", imageUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ui/feed")
    public ResponseEntity<Page<OverviewFeedDto>> getCampaignFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(campaignService.getOverviewFeed(page, size));
    }

    @GetMapping("/ui/{campaignId}/details")
    public ResponseEntity<com.kryxhub.kryxhub.campaign.dto.CampaignDetailsDto> getCampaignUIDetails(
            @PathVariable java.util.UUID campaignId) {
        
        com.kryxhub.kryxhub.campaign.dto.CampaignDetailsDto details = campaignService.getCampaignDetails(campaignId);
        return ResponseEntity.ok(details);
    }
}