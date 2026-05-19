package com.kryxhub.kryxhub.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kryxhub.kryxhub.campaign.enums.Platforms;
import com.kryxhub.kryxhub.submission.dto.VideoStatsDto;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VimeoApiService implements PlatformApiService {

    @Value("${vimeo.access-token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Platforms getPlatform() {
        return Platforms.VIMEO;
    }

    @Override
    public VideoStatsDto fetchVideoStats(String videoUrl) {

        try {

            String videoId = extractVimeoId(videoUrl);
            if (videoId == null) throw new RuntimeException("Invalid Vimeo URL");

            String apiUrl = "https://api.vimeo.com/videos/" + videoId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            JsonNode body = objectMapper.readTree(response.getBody());

            if (body != null && body.has("name")) {
                String title = body.get("name").asText();
                int plays = body.has("stats") && body.get("stats").has("plays") && !body.get("stats").get("plays").isNull() 
                            ? body.get("stats").get("plays").asInt() : 0;
                OffsetDateTime publishedAt = OffsetDateTime.parse(body.get("release_time").asText());

                return new VideoStatsDto(title, plays, publishedAt);
            }
            throw new RuntimeException("Could not fetch Vimeo data");

        } catch (Exception e) {
            throw new RuntimeException("Error parsing Vimeo API: " + e.getMessage());
        }
    }

    private String extractVimeoId(String url) {
        if (url == null || url.isEmpty()) return null;
        
        String regex = "vimeo\\.com\\/(?:.*\\/)?(\\d+)";
        Matcher matcher = Pattern.compile(regex).matcher(url);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}