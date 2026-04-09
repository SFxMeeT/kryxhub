package com.kryxhub.kryxhub.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/config")
public class ConfigController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @GetMapping("/google-client-id")
    public ResponseEntity<String> getGoogleClientId() {
        return ResponseEntity.ok(googleClientId);
    }
}
