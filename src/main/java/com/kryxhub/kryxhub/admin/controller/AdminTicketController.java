package com.kryxhub.kryxhub.admin.controller;

import com.kryxhub.kryxhub.communication.enums.TicketStatus;
import com.kryxhub.kryxhub.communication.service.SupportTicketService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tickets")
public class AdminTicketController {

    private final SupportTicketService ticketService;

    public AdminTicketController(SupportTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID ticketId, 
            @RequestParam TicketStatus newStatus,
            @RequestParam(required = false) String resolutionNote) {
        
        String result = ticketService.updateTicketStatus(ticketId, newStatus, resolutionNote);
        return ResponseEntity.ok(Map.of("message", result));
    }
}