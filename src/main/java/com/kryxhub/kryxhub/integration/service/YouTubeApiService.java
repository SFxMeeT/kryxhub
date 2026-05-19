package com.kryxhub.kryxhub.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kryxhub.kryxhub.campaign.enums.Platforms;
import com.kryxhub.kryxhub.submission.dto.VideoStatsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YouTubeApiService implements PlatformApiService {

    @Value("${youtube.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Platforms getPlatform() {
        return Platforms.YOUTUBE;
    }

    @Override
    public VideoStatsDto fetchVideoStats(String videoUrl) {
        String videoId = extractYouTubeId(videoUrl);
        if (videoId == null) throw new RuntimeException("Invalid YouTube URL");

        String apiUrl = String.format("https://www.googleapis.com/youtube/v3/videos?part=snippet,statistics&id=%s&key=%s", videoId, apiKey);
        
        JsonNode response = restTemplate.getForObject(apiUrl, JsonNode.class);

        if (response != null && response.has("items") && response.get("items").size() > 0) {
            JsonNode videoData = response.get("items").get(0);
            String title = videoData.get("snippet").get("title").asText();
            int views = videoData.get("statistics").get("viewCount").asInt();
            OffsetDateTime publishedAt = OffsetDateTime.parse(videoData.get("snippet").get("publishedAt").asText());

            return new VideoStatsDto(title, views, publishedAt);
        }
        throw new RuntimeException("Could not fetch YouTube data");
    }

    private String extractYouTubeId(String url) {
        if (url == null || url.isEmpty()) return null;
        
        String regex = "(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|shorts\\/|live\\/|watch\\?v=|watch\\?.+&v=))([a-zA-Z0-9_-]{11})";
        
        Matcher matcher = Pattern.compile(regex).matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}