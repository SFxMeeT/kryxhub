package com.kryxhub.kryxhub.integration.service;

import com.kryxhub.kryxhub.campaign.enums.Platforms;
import com.kryxhub.kryxhub.submission.dto.VideoStatsDto;

import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
public class VimeoApiService implements PlatformApiService {

    @Override
    public Platforms getPlatform() {
        return Platforms.VIMEO;
    }

    @Override
    public VideoStatsDto fetchVideoStats(String videoUrl) {
        // TODO: Later, use RestTemplate or WebClient to call: 
        // https://www.googleapis.com/youtube/v3/videos?part=statistics,snippet&id={VIDEO_ID}&key={YOUR_API_KEY}
        
        // For now, return mock data so we can test the 30-minute rule!
        // Simulating a video uploaded exactly 10 minutes ago with 1,500 views
        return new VideoStatsDto(
                "Extracted Vimeo Title", 
                1500, 
                OffsetDateTime.now().minusMinutes(10) 
        );
    }
}