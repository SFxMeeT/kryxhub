package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.enums.Platforms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PlatformVerificationService {

    private final RestTemplate restTemplate;

    public PlatformVerificationService() {
        this.restTemplate = new RestTemplate();
    }

    @Value("${youtube.api-key}")
    private String youtubeApiKey;

    @Value("${vimeo.access-token}")
    private String vimeoAccessToken;

    @Value("${twitch.client-id}")
    private String twitchClientId;

    @Value("${twitch.client-secret}")
    private String twitchClientSecret;

    public boolean verifyCodeInBio(Platforms platform, String username, String expectedCode) {
        try {
            return switch (platform) {
                case REDDIT -> verifyRedditBio(username, expectedCode);
                case YOUTUBE -> verifyYouTubeBio(username, expectedCode);
                case TWITCH -> verifyTwitchBio(username, expectedCode);
                case VIMEO -> verifyVimeoBio(username, expectedCode);
                default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
            };
        } catch (Exception e) {
            System.err.println("Verification failed for " + username + " on " + platform + ": " + e.getMessage());
            return false;
        }
    }

    private boolean verifyRedditBio(String username, String expectedCode) {
        String url = "https://www.reddit.com/user/" + username + "/about.json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "KryxHub-Verification-Bot/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> body = response.getBody();

        if (body != null && body.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            Map<String, Object> subreddit = (Map<String, Object>) data.get("subreddit");

            if (subreddit != null && subreddit.containsKey("public_description")) {
                String bio = (String) subreddit.get("public_description");

                return bio != null && bio.contains(expectedCode);
            }
        }
        return false;
    }

    private boolean verifyYouTubeBio(String username, String expectedCode) {

        String handle = username.startsWith("@") ? username : "@" + username;

        String url = "https://www.googleapis.com/youtube/v3/channels?part=snippet&forHandle=" + handle + "&key=" + youtubeApiKey;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

                if (items != null && !items.isEmpty()) {
                    Map<String, Object> snippet = (Map<String, Object>) items.get(0).get("snippet");

                    if (snippet != null && snippet.containsKey("description")) {
                        String description = (String) snippet.get("description");

                        return description != null && description.contains(expectedCode);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("YouTube API error for handle " + handle + ": " + e.getMessage());
        }

        return false;
    }

    private boolean verifyTwitchBio(String username, String expectedCode) {
        try {

            String tokenUrl = "https://id.twitch.tv/oauth2/token";

            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> tokenBody = new LinkedMultiValueMap<>();
            tokenBody.add("client_id", twitchClientId);
            tokenBody.add("client_secret", twitchClientSecret);
            tokenBody.add("grant_type", "client_credentials");

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenBody, tokenHeaders);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);

            String accessToken = (String) tokenResponse.getBody().get("access_token");

            String userUrl = "https://api.twitch.tv/helix/users?login=" + username;

            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            userHeaders.set("Client-Id", twitchClientId);

            HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);
            ResponseEntity<Map> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, userRequest, Map.class);

            Map<String, Object> body = userResponse.getBody();

            if (body != null && body.containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) body.get("data");

                if (data != null && !data.isEmpty()) {
                    String description = (String) data.get(0).get("description");

                    return description != null && description.contains(expectedCode);
                }
            }

        } catch (Exception e) {
            System.err.println("Twitch API error for username " + username + ": " + e.getMessage());
        }

        return false;
    }

    private boolean verifyVimeoBio(String username, String expectedCode) {

        String url = "https://api.vimeo.com/users/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(vimeoAccessToken);
        headers.set("Accept", "application/vnd.vimeo.*+json;version=3.4");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("bio")) {
                Object bioObj = body.get("bio");

                if (bioObj instanceof String) {
                    String bio = (String) bioObj;

                    return bio.contains(expectedCode);
                }
            }
        } catch (Exception e) {
            System.err.println("Vimeo API error for username " + username + ": " + e.getMessage());
        }

        return false;
    }
}
