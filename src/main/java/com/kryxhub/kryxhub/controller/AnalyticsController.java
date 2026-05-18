package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.CreatorAnalyticsDto;
import com.kryxhub.kryxhub.enums.CampaignType;
import com.kryxhub.kryxhub.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
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