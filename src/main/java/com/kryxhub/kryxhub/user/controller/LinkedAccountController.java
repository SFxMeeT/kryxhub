package com.kryxhub.kryxhub.user.controller;

import com.kryxhub.kryxhub.user.dto.LinkedAccountsResponseDto;
import com.kryxhub.kryxhub.user.service.LinkedAccountService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class LinkedAccountController {

    private final LinkedAccountService linkedAccountService;

    public LinkedAccountController(LinkedAccountService linkedAccountService) {
        this.linkedAccountService = linkedAccountService;
    }

    @GetMapping("/ui/linked")
    public ResponseEntity<List<LinkedAccountsResponseDto>> getLinkedAccountsUi(
            @AuthenticationPrincipal Jwt jwt) {

        String userEmail = jwt.getSubject();
        
        List<LinkedAccountsResponseDto> response = linkedAccountService.getGroupedLinkedAccounts(userEmail);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/ui/linked/{accountId}")
    public ResponseEntity<String> unlinkAccount(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID accountId) {

        String userEmail = jwt.getSubject();

        String responseMessage = linkedAccountService.unlinkAccount(accountId, userEmail);
        
        return ResponseEntity.ok(responseMessage);
    }
}