package com.kryxhub.kryxhub.entity;

import com.kryxhub.kryxhub.enums.TicketStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "support_tickets", indexes = {
        @Index(name = "idx_tickets_user_id", columnList = "user_id")
})
public class SupportTicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payout_id")
    private PayoutEntity payout;

    @Column(nullable = false, length = 255)
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status = TicketStatus.OPEN;

    @Column(name = "due_date")
    private OffsetDateTime dueDate;

    @Column(name = "closed_on")
    private OffsetDateTime closedOn;

    @Column(name = "opened_on", nullable = false, updatable = false)
    private OffsetDateTime openedOn;

    public SupportTicketEntity() {
    }

    @PrePersist
    protected void onOpen() {
        this.openedOn = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public PayoutEntity getPayout() {
        return payout;
    }

    public void setPayout(PayoutEntity payout) {
        this.payout = payout;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public OffsetDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(OffsetDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public OffsetDateTime getClosedOn() {
        return closedOn;
    }

    public OffsetDateTime getOpenedOn() {
        return openedOn;
    }
}
