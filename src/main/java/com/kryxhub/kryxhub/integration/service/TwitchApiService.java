package com.kryxhub.kryxhub.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kryxhub.kryxhub.campaign.enums.Platforms;
import com.kryxhub.kryxhub.submission.dto.VideoStatsDto;
import org.springframework.beans.factory.annotation.Value;
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
public class TwitchApiService implements PlatformApiService {

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Platforms getPlatform() {
        return Platforms.TWITCH;
    }

    @Override
    public VideoStatsDto fetchVideoStats(String videoUrl) {
        String videoId = extractTwitchId(videoUrl);
        if (videoId == null) throw new RuntimeException("Invalid Twitch URL");

        String tokenUrl = String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials", clientId, clientSecret);
        JsonNode tokenResponse = restTemplate.postForObject(tokenUrl, null, JsonNode.class);
        String accessToken = tokenResponse.get("access_token").asText();

        String apiUrl = "https://api.twitch.tv/helix/videos?id=" + videoId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", clientId);
        headers.set("Authorization", "Bearer " + accessToken);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
        JsonNode body = response.getBody();

        if (body != null && body.has("data") && body.get("data").size() > 0) {
            JsonNode videoData = body.get("data").get(0);
            String title = videoData.get("title").asText();
            int views = videoData.get("view_count").asInt();
            OffsetDateTime publishedAt = OffsetDateTime.parse(videoData.get("created_at").asText());

            return new VideoStatsDto(title, views, publishedAt);
        }
        throw new RuntimeException("Could not fetch Twitch data");
    }

    private String extractTwitchId(String url) {
        Matcher matcher = Pattern.compile("videos/(\\d+)").matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }
}