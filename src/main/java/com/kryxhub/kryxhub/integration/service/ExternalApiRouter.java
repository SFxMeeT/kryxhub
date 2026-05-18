package com.kryxhub.kryxhub.integration.service;

import com.kryxhub.kryxhub.campaign.enums.Platforms;
import com.kryxhub.kryxhub.submission.dto.VideoStatsDto;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExternalApiRouter {

    private final Map<Platforms, PlatformApiService> platformServices;
    
    public ExternalApiRouter(List<PlatformApiService> services) {

        this.platformServices = services.stream()
                .collect(Collectors.toMap(PlatformApiService::getPlatform, service -> service));
    }

    public VideoStatsDto getStats(Platforms platform, String videoUrl) {
        PlatformApiService service = platformServices.get(platform);
        
        if (service == null) {
            throw new RuntimeException("API integration for " + platform + " is not yet implemented.");
        }
        
        return service.fetchVideoStats(videoUrl);
    }
}