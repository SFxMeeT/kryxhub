package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.AdminCampaignDto;
import com.kryxhub.kryxhub.service.CampaignService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/campaigns")
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
}