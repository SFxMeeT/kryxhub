package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.VideoStatsDto;
import com.kryxhub.kryxhub.enums.Platforms;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExternalApiRouter {

    private final Map<Platforms, PlatformApiService> platformServices;

    // Spring magically injects a List of every class that implements PlatformApiService!
    public ExternalApiRouter(List<PlatformApiService> services) {
        // Convert the list into a Map for instant lookups
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