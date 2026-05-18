package com.kryxhub.kryxhub.admin.controller;

import com.kryxhub.kryxhub.admin.dto.AdminUserDto;
import com.kryxhub.kryxhub.analytics.dto.UserActivityDto;
import com.kryxhub.kryxhub.user.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
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
    public ResponseEntity<?> toggleSuspendUser(@PathVariable java.util.UUID userId) {
        try {
            String resultMessage = userService.toggleUserSuspension(userId);
            return ResponseEntity.ok(java.util.Map.of(
                    "status", "success",
                    "message", resultMessage
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/activity")
    public ResponseEntity<UserActivityDto> getUserActivity(@PathVariable java.util.UUID userId) {
        try {
            UserActivityDto activity = userService.getUserActivityTracker(userId);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}