package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.VideoStatsDto;
import com.kryxhub.kryxhub.enums.Platforms;

public interface PlatformApiService {
    
    Platforms getPlatform(); 

    VideoStatsDto fetchVideoStats(String videoUrl); 
}