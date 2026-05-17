package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.NotificationDto;
import com.kryxhub.kryxhub.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET /api/notifications?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getMyNotifications(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<NotificationDto> notifications = notificationService
                .getUserNotifications(auth.getName(), page, size)
                .map(NotificationDto::new);
                
        return ResponseEntity.ok(notifications);
    }

    // PUT /api/notifications/{id}/read
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(java.util.Map.of("status", "success", "message", "Marked as read"));
    }
}