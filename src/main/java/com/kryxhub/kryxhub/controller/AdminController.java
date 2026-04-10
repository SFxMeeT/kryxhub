package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.AdminUserResponse;
import com.kryxhub.kryxhub.dto.AdminUserUpdateRequest;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AdminUserService adminUserService, PasswordEncoder passwordEncoder) {
        this.adminUserService = adminUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/get-hash-password")
    public String getHashPassword(@RequestBody String pwd) {
        return passwordEncoder.encode(pwd);
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable UUID userId, @RequestBody AdminUserUpdateRequest request) {
        try {
            UserEntity updatedUser = adminUserService.updateUserAsAdmin(userId, request);

            AdminUserResponse responseBody = new AdminUserResponse(updatedUser);

            return ResponseEntity.ok(responseBody);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
