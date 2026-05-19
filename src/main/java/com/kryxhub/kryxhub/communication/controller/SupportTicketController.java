package com.kryxhub.kryxhub.communication.controller;

import com.kryxhub.kryxhub.communication.dto.SupportTicketDto;
import com.kryxhub.kryxhub.communication.service.SupportTicketService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "7. Communications")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    public SupportTicketController(SupportTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<SupportTicketDto.Response> createTicket(
            Authentication auth, 
            @RequestBody SupportTicketDto.CreateRequest request) {
        return ResponseEntity.ok(ticketService.createTicket(auth.getName(), request));
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<Page<SupportTicketDto.Response>> getMyTickets(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ticketService.getUserTickets(auth.getName(), page, size));
    }
}