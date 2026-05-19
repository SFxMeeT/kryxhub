package com.kryxhub.kryxhub.analytics.controller;

import com.kryxhub.kryxhub.analytics.dto.FunderAnalyticsDto;
import com.kryxhub.kryxhub.analytics.service.FunderAnalyticsService;
import com.kryxhub.kryxhub.campaign.enums.CampaignType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funders/ui/analytics")
public class FunderAnalyticsController {

    private final FunderAnalyticsService analyticsService;

    public FunderAnalyticsController(FunderAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<FunderAnalyticsDto> getFunderAnalytics(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "30_DAYS") String timeframe,
            @RequestParam(required = false) CampaignType campaignType) {

        String funderEmail = jwt.getSubject();
        
        FunderAnalyticsDto analytics = analyticsService.getFunderAnalytics(funderEmail, timeframe, campaignType);
        
        return ResponseEntity.ok(analytics);
    }
}