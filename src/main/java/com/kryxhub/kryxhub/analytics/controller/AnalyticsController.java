package com.kryxhub.kryxhub.analytics.controller;

import com.kryxhub.kryxhub.analytics.dto.CreatorAnalyticsDto;
import com.kryxhub.kryxhub.analytics.service.AnalyticsService;
import com.kryxhub.kryxhub.campaign.enums.CampaignType;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "5. Analytics & Dashboards", description = "Metrics and data visualization for creators and funders")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/ui/creator")
    public ResponseEntity<CreatorAnalyticsDto> getCreatorAnalytics(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "30_DAYS") String timeframe,
            @RequestParam(required = false) CampaignType campaignType) {

        String creatorEmail = jwt.getSubject();
        
        CreatorAnalyticsDto analytics = analyticsService.getCreatorAnalytics(creatorEmail, timeframe, campaignType);
        
        return ResponseEntity.ok(analytics);
    }
}