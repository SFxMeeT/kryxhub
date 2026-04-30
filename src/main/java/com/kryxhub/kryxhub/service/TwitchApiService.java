package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.VideoStatsDto;
import com.kryxhub.kryxhub.enums.Platforms;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
public class TwitchApiService implements PlatformApiService {

    @Override
    public Platforms getPlatform() {
        return Platforms.TWITCH;
    }

    @Override
    public VideoStatsDto fetchVideoStats(String videoUrl) {
        // TODO: Later, use RestTemplate or WebClient to call: 
        // https://www.googleapis.com/youtube/v3/videos?part=statistics,snippet&id={VIDEO_ID}&key={YOUR_API_KEY}
        
        // For now, return mock data so we can test the 30-minute rule!
        // Simulating a video uploaded exactly 10 minutes ago with 1,500 views
        return new VideoStatsDto(
                "Extracted Twitch Title", 
                1500, 
                OffsetDateTime.now().minusMinutes(10) 
        );
    }
}