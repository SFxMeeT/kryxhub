package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.*;
import com.kryxhub.kryxhub.service.ConnectedAppService;
import com.kryxhub.kryxhub.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ConnectedAppService connectedAppService;

    public UserController(UserService userService, ConnectedAppService connectedAppService) {
        this.userService = userService;
        this.connectedAppService = connectedAppService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Principal principal) {
        UserProfileResponse profile = userService.getUserProfile(principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(Principal principal, @RequestBody UpdateProfileRequest request) {
        UserProfileResponse updatedProfile = userService.updateUserProfile(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProfile);
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyAccount(Principal principal) {
        userService.requestAccountDeletion(principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Account successfully scheduled for deletion.\"}");
    }

    @PostMapping("/me/social-accounts/initiate")
    public ResponseEntity<InitiateLinkResponse> initiateSocialLink(Principal principal, @RequestBody InitiateLinkRequest request) {

        InitiateLinkResponse response = connectedAppService.initiateAccountLink(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/me/social-accounts/{id}/verify")
    public ResponseEntity<?> confirmSocialLink(Principal principal, @PathVariable UUID id) {

        boolean success = connectedAppService.confirmAccountLink(principal.getName(), id);

        if (success) {
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Account successfully verified!\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Verification code not found in bio. Please make sure you saved it and try again.\"}");
        }
    }

    @PostMapping("/me/email/request-change")
    public ResponseEntity<?> requestEmailChange(Principal principal, @RequestBody EmailChangeRequest request) {
        try {
            userService.requestEmailChange(principal.getName(), request);
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Verification code sent to new email.\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/me/email/verify")
    public ResponseEntity<?> verifyEmailChange(Principal principal, @RequestBody VerifyEmailChangeRequest request) {
        try {
            AuthCookieAccess newAuthContext = userService.confirmEmailChange(principal.getName(), request);

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, newAuthContext.getCookie().toString())
                    .body(newAuthContext.getAuthAccessResponse());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/me/2fa/request-enable")
    public ResponseEntity<?> request2faEnable(Principal principal) {
        userService.request2faEnable(principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"2FA code sent to your email.\"}");
    }

    @PostMapping("/me/2fa/verify-enable")
    public ResponseEntity<?> verify2faEnable(Principal principal, @RequestBody Verify2faEnableRequest request) {
        userService.confirm2faEnable(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Two-Factor Authentication successfully enabled!\"}");
    }
}