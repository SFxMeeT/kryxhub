package com.kryxhub.kryxhub.entity;

import com.kryxhub.kryxhub.enums.PayoutStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payouts", indexes = {
        @Index(name = "idx_payouts_creator_id", columnList = "creator_id"),
        @Index(name = "idx_payouts_submission_id", columnList = "submission_id"),
        @Index(name = "idx_payouts_status", columnList = "status")
})
public class PayoutsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private SubmissionsEntity submission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private UsersEntity creator;

    @Column(name = "amount_gross", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountGross;

    @Column(name = "platform_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal platformFee = BigDecimal.ZERO;

    @Column(name = "amount_net", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountNet;

    @Column(name = "stripe_transfer_id", unique = true, length = 255)
    private String stripeTransferId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PayoutStatus status = PayoutStatus.PENDING;

    @Column(name = "scheduled_for")
    private OffsetDateTime scheduledFor;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "payout", cascade = CascadeType.ALL)
    private List<SupportTicketsEntity> supportTickets = new ArrayList<>();

    public PayoutsEntity() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public SubmissionsEntity getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionsEntity submission) {
        this.submission = submission;
    }

    public UsersEntity getCreator() {
        return creator;
    }

    public void setCreator(UsersEntity creator) {
        this.creator = creator;
    }

    public BigDecimal getAmountGross() {
        return amountGross;
    }

    public void setAmountGross(BigDecimal amountGross) {
        this.amountGross = amountGross;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getAmountNet() {
        return amountNet;
    }

    public void setAmountNet(BigDecimal amountNet) {
        this.amountNet = amountNet;
    }

    public String getStripeTransferId() {
        return stripeTransferId;
    }

    public void setStripeTransferId(String stripeTransferId) {
        this.stripeTransferId = stripeTransferId;
    }

    public PayoutStatus getStatus() {
        return status;
    }

    public void setStatus(PayoutStatus status) {
        this.status = status;
    }

    public OffsetDateTime getScheduledFor() {
        return scheduledFor;
    }

    public OffsetDateTime getPaidAt() {
        return paidAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<SupportTicketsEntity> getSupportTickets() {
        return supportTickets;
    }
}
