package com.kryxhub.kryxhub.admin.controller;

import com.kryxhub.kryxhub.admin.dto.AdminUserDto;
import com.kryxhub.kryxhub.analytics.dto.UserActivityDto;
import com.kryxhub.kryxhub.user.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "8. Admin & Moderation", description = "Internal platform management tools for KryxHub staff")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminUserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<AdminUserDto> userFeed = userService.getAllUsersForAdmin(page, size);
        return ResponseEntity.ok(userFeed);
    }
    
    @PutMapping("/{userId}/suspend")
    public ResponseEntity<?> toggleSuspendUser(@PathVariable UUID userId) {
        try {
            String resultMessage = userService.toggleUserSuspension(userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", resultMessage
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/activity")
    public ResponseEntity<UserActivityDto> getUserActivity(@PathVariable UUID userId) {
        try {
            UserActivityDto activity = userService.getUserActivityTracker(userId);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}