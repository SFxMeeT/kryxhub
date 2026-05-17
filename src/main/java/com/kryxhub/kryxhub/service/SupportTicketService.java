package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.SupportTicketDto;
import com.kryxhub.kryxhub.entity.PayoutEntity;
import com.kryxhub.kryxhub.entity.SupportTicketEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.enums.TicketStatus;
import com.kryxhub.kryxhub.repository.PayoutRepository;
import com.kryxhub.kryxhub.repository.SupportTicketRepository;
import com.kryxhub.kryxhub.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final PayoutRepository payoutRepository;
    private final NotificationService notificationService;

    public SupportTicketService(SupportTicketRepository ticketRepository, UserRepository userRepository, PayoutRepository payoutRepository, NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.payoutRepository = payoutRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public SupportTicketDto.Response createTicket(String email, SupportTicketDto.CreateRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicketEntity ticket = new SupportTicketEntity();
        ticket.setUser(user);
        ticket.setTopic(request.getTopic());
        ticket.setDescription(request.getDescription());
        ticket.setAmount(request.getAmount());
        
        ticket.setDueDate(OffsetDateTime.now().plusDays(3)); 

        if (request.getPayoutId() != null) {
            PayoutEntity payout = payoutRepository.findById(request.getPayoutId())
                    .orElseThrow(() -> new RuntimeException("Payout not found"));
            ticket.setPayout(payout);
        }

        ticket = ticketRepository.save(ticket);

        return mapToResponse(ticket);
    }

    public Page<SupportTicketDto.Response> getUserTickets(String email, int page, int size) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        return ticketRepository.findByUserOrderByOpenedOnDesc(user, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public String updateTicketStatus(UUID ticketId, TicketStatus newStatus, String resolutionNote) {
        SupportTicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(newStatus);

        if (resolutionNote != null && !resolutionNote.trim().isEmpty()) {
            ticket.setResolutionNote(resolutionNote);
        }

        if (newStatus == TicketStatus.RESOLVED || newStatus == TicketStatus.CLOSED) {
            ticket.setClosedOn(java.time.OffsetDateTime.now());

            notificationService.createAndSend(
                    ticket.getUser(),
                    "Support Ticket Resolved",
                    "Your ticket regarding '" + ticket.getTopic() + "' has been resolved.",
                    "SUPPORT",
                    "/settings/resolution"
            );
        } else {
            ticket.setClosedOn(null); 
        }

        ticketRepository.save(ticket);
        return "Ticket " + ticketId + " updated to " + newStatus.name();
    }

    private SupportTicketDto.Response mapToResponse(SupportTicketEntity t) {
        return new SupportTicketDto.Response(
                t.getId(), t.getTopic(), t.getStatus(), t.getAmount(), 
                t.getOpenedOn(), t.getDueDate(), t.getClosedOn()
        );
    }
}