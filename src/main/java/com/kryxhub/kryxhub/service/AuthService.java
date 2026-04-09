package com.kryxhub.kryxhub.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kryxhub.kryxhub.dto.*;
import com.kryxhub.kryxhub.entity.RefreshTokenEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.enums.AccountStatus;
import com.kryxhub.kryxhub.enums.Role;
import com.kryxhub.kryxhub.enums.TokenType;
import com.kryxhub.kryxhub.repository.UserRepository;
import com.kryxhub.kryxhub.security.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final AuthUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final RestTemplate restTemplate;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenService jwtTokenService,
                       RefreshTokenService refreshTokenService,
                       AuthUserDetailsService userDetailsService,
                       UserRepository userRepository,
                       TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
        this.restTemplate = new RestTemplate();
    }

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.discord.client-id}")
    private String discordClientId;

    @Value("${spring.security.oauth2.client.registration.discord.client-secret}")
    private String discordClientSecret;

    @Value("${spring.security.oauth2.client.registration.discord.redirect-uri}")
    private String discordRedirectUri;

    public AuthResponse authenticate(AuthRequest authRequest) {
        var loginToken = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(loginToken);

        String accessToken = jwtTokenService.generateToken(authentication);
        Long expiresAt = jwtTokenService.extractExpirationTime(accessToken);

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail());

        return new AuthResponse(accessToken, refreshToken.getToken(), authentication.getName(), expiresAt);
    }

    public AuthCookieAccess login(AuthRequest authRequest) {

        AuthResponse authenticate = authenticate(authRequest);

        AuthAccessResponse authAccessResponse = new AuthAccessResponse(
                authenticate.getAccessToken(),
                authenticate.getEmail(),
                authenticate.getExpiresAt(),
                TokenType.ACCESS_TOKEN
        );

        return new AuthCookieAccess(ResponseCookie.from("refresh_jwt", authenticate.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .build(),
                authAccessResponse);
    }

    public ResponseCookie logout(Jwt jwt) {

        long remainingTime = jwt.getExpiresAt().toEpochMilli() - Instant.now().toEpochMilli();

        if (remainingTime > 0) {
            tokenBlacklistService.addToBlacklist(jwt.getTokenValue(), remainingTime);
        }

        userRepository.findByEmail(jwt.getSubject()).ifPresent(refreshTokenService::deleteByUserId);
        return ResponseCookie.from("refresh_jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0)
                .build();
    }

    public AuthCookieAccess refreshToken(RefreshTokenRequest request) {
        AuthResponse authResponse = refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    String newAccessToken = jwtTokenService.generateToken(authentication);
                    Long expiresAt = jwtTokenService.extractExpirationTime(newAccessToken);

                    return new AuthResponse(newAccessToken, request.getRefreshToken(), user.getEmail(), expiresAt);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));

        AuthAccessResponse authAccessResponse = new AuthAccessResponse(
                authResponse.getAccessToken(),
                authResponse.getEmail(),
                authResponse.getExpiresAt(),
                TokenType.ACCESS_TOKEN
        );

        return new AuthCookieAccess(ResponseCookie.from("refresh_jwt", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .build(),
                authAccessResponse);
    }

    public AuthCookieAccess authGoogleLogin(String googleTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String googleId = payload.getSubject();

                return executeAssimilation(email, name, "GOOGLE", googleId, pictureUrl);
            } else {
                throw new RuntimeException("Google Token is mathematically invalid or expired.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google Token: " + e.getMessage());
        }
    }

    public AuthCookieAccess authDiscordLogin(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", discordClientId);
            body.add("client_secret", discordClientSecret);
            body.add("grant_type", "authorization_code");
            body.add("code", code);
            body.add("redirect_uri", discordRedirectUri);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity("https://discord.com/api/oauth2/token", request, Map.class);
            String discordAccessToken = (String) tokenResponse.getBody().get("access_token");

            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(discordAccessToken);
            HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);

            ResponseEntity<Map> userResponse = restTemplate.exchange("https://discord.com/api/users/@me", HttpMethod.GET, userRequest, Map.class);
            Map<String, Object> userData = userResponse.getBody();

            String discordId = (String) userData.get("id");
            String email = (String) userData.get("email");
            String name = (String) userData.get("global_name");
            if (name == null) name = (String) userData.get("username");

            String avatarHash = (String) userData.get("avatar");
            String pictureUrl = avatarHash != null ? "https://cdn.discordapp.com/avatars/" + discordId + "/" + avatarHash + ".png" : null;

            return executeAssimilation(email, name, "DISCORD", discordId, pictureUrl);

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Discord Code: " + e.getMessage());
        }
    }

    private AuthCookieAccess executeAssimilation(String email, String displayName, String platform, String platformId, String pictureUrl) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        UserEntity user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            boolean updated = false;

            if (platform.equals("GOOGLE") && user.getGoogleId() == null) {
                user.setGoogleId(platformId);
                updated = true;
            } else if (platform.equals("DISCORD") && user.getDiscordId() == null) {
                user.setDiscordId(platformId);
                updated = true;
            }

            if (user.getProfilePicUrl() == null && pictureUrl != null) {
                user.setProfilePicUrl(pictureUrl);
                updated = true;
            }

            if (updated) {
                user = userRepository.save(user);
            }
        } else {
            user = new UserEntity();
            user.setEmail(email);
            user.setDisplayName(displayName);

            if (platform.equals("GOOGLE")) user.setGoogleId(platformId);
            if (platform.equals("DISCORD")) user.setDiscordId(platformId);

            user.setProfilePicUrl(pictureUrl);
            user.setUsername(email.split("@")[0] + UUID.randomUUID().toString().substring(0, 5));
            user.setRole(Role.CREATOR);
            user.setAccountStatus(AccountStatus.ACTIVE);
            user = userRepository.save(user);
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        String accessToken = jwtTokenService.generateToken(authentication);
        Long expiresAt = jwtTokenService.extractExpirationTime(accessToken);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        AuthAccessResponse authAccessResponse = new AuthAccessResponse(
                accessToken, user.getEmail(), expiresAt, TokenType.ACCESS_TOKEN
        );

        return new AuthCookieAccess(
                ResponseCookie.from("refresh_jwt", refreshToken.getToken())
                        .httpOnly(true)
                        .secure(true)
                        .path("/api/auth/refresh")
                        .maxAge(7 * 24 * 60 * 60)
                        .build(),
                authAccessResponse
        );
    }
}
