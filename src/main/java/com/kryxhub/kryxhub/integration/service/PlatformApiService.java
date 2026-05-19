package com.kryxhub.kryxhub.integration.service;

import com.kryxhub.kryxhub.campaign.enums.Platforms;
import com.kryxhub.kryxhub.submission.dto.VideoStatsDto;

public interface PlatformApiService {
    
    Platforms getPlatform(); 

    VideoStatsDto fetchVideoStats(String videoUrl); 
}