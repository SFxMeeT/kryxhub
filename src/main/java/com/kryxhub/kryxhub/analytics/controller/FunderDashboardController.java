package com.kryxhub.kryxhub.analytics.controller;

import com.kryxhub.kryxhub.analytics.dto.FunderCampaignCardDto;
import com.kryxhub.kryxhub.analytics.dto.FunderMetricsDto;
import com.kryxhub.kryxhub.analytics.service.FunderDashboardService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funders/ui/dashboard")
@Tag(name = "5. Analytics & Dashboards", description = "Metrics and data visualization for creators and funders")
public class FunderDashboardController {

    private final FunderDashboardService dashboardService;

    public FunderDashboardController(FunderDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<FunderMetricsDto> getDashboardMetrics(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "7D") String timeframe) {
        
        String funderEmail = jwt.getSubject();
        FunderMetricsDto metrics = dashboardService.getFunderMetrics(funderEmail, timeframe);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<Page<FunderCampaignCardDto>> getDashboardCampaigns(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "ALL") String tabFilter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String funderEmail = jwt.getSubject();
        Page<FunderCampaignCardDto> campaigns = dashboardService.getFunderCampaigns(funderEmail, tabFilter, page, size);
        return ResponseEntity.ok(campaigns);
    }
}