package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.CampaignDiscoveryDto;
import com.kryxhub.kryxhub.enums.CampaignCategory;
import com.kryxhub.kryxhub.enums.CampaignType;
import com.kryxhub.kryxhub.enums.Platforms;
import com.kryxhub.kryxhub.service.CampaignService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns")
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