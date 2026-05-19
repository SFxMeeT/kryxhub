package com.kryxhub.kryxhub.campaign.controller;

import com.kryxhub.kryxhub.campaign.dto.CampaignDiscoveryDto;
import com.kryxhub.kryxhub.campaign.enums.CampaignCategory;
import com.kryxhub.kryxhub.campaign.enums.CampaignType;
import com.kryxhub.kryxhub.campaign.enums.Platforms;
import com.kryxhub.kryxhub.campaign.service.CampaignService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns")
@Tag(name = "3. Campaigns", description = "Funder campaign creation, management, and discovery")
public class CampaignDiscoveryController {

    private final CampaignService campaignService;

    public CampaignDiscoveryController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/ui/discover")
    public ResponseEntity<Page<CampaignDiscoveryDto>> discoverCampaigns(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CampaignCategory category,
            @RequestParam(required = false) CampaignType type,
            @RequestParam(required = false) Platforms platform,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<CampaignDiscoveryDto> feed = campaignService.getDiscoveryFeed(
                keyword, category, type, platform, page, size);
        
        return ResponseEntity.ok(feed);
    }
}