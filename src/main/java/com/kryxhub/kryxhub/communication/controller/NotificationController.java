package com.kryxhub.kryxhub.communication.controller;

import com.kryxhub.kryxhub.communication.dto.NotificationDto;
import com.kryxhub.kryxhub.communication.service.NotificationService;

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

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(java.util.Map.of("status", "success", "message", "Marked as read"));
    }
}