package com.kryxhub.kryxhub.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kryxhub.kryxhub.campaign.enums.Platforms;
import com.kryxhub.kryxhub.submission.dto.VideoStatsDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RedditApiService implements PlatformApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Platforms getPlatform() {
        return Platforms.REDDIT;
    }

    @Override
    public VideoStatsDto fetchVideoStats(String videoUrl) {

        String postId = extractRedditId(videoUrl);
        if (postId == null) throw new RuntimeException("Invalid Reddit URL");

        String apiUrl = "https://www.reddit.com/comments/" + postId + ".json";;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "KryxHub-Backend/1.0 (Integration Testing)");
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
        JsonNode body = response.getBody();

        if (body != null && body.isArray() && body.size() > 0) {
            JsonNode postData = body.get(0).get("data").get("children").get(0).get("data");
            
            String title = postData.get("title").asText();
            int score = postData.get("score").asInt();
            long createdUtc = postData.get("created_utc").asLong();
            
            OffsetDateTime publishedAt = Instant.ofEpochSecond(createdUtc).atOffset(ZoneOffset.UTC);

            return new VideoStatsDto(title, score, publishedAt);
        }
        throw new RuntimeException("Could not fetch Reddit data");
    }

    private String extractRedditId(String url) {
        if (url == null || url.isEmpty()) return null;
        
        String regex = "(?:comments\\/|redd\\.it\\/)([a-zA-Z0-9]{5,9})";
        Matcher matcher = Pattern.compile(regex).matcher(url);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}