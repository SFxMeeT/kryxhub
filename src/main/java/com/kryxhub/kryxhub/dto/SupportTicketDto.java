package com.kryxhub.kryxhub.dto;

import com.kryxhub.kryxhub.enums.TicketStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class SupportTicketDto {

    public static class CreateRequest {
        private String topic;
        private String description;
        private UUID payoutId;
        private BigDecimal amount;

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public UUID getPayoutId() { return payoutId; }
        public void setPayoutId(UUID payoutId) { this.payoutId = payoutId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static class Response {
        private UUID id;
        private String topic;
        private TicketStatus status;
        private BigDecimal amount;
        private OffsetDateTime openedOn;
        private OffsetDateTime dueDate;
        private OffsetDateTime closedOn;

        public Response(UUID id, String topic, TicketStatus status, BigDecimal amount, 
                        OffsetDateTime openedOn, OffsetDateTime dueDate, OffsetDateTime closedOn) {
            this.id = id;
            this.topic = topic;
            this.status = status;
            this.amount = amount;
            this.openedOn = openedOn;
            this.dueDate = dueDate;
            this.closedOn = closedOn;
        }

        public UUID getId() { return id; }
        public String getTopic() { return topic; }
        public TicketStatus getStatus() { return status; }
        public BigDecimal getAmount() { return amount; }
        public OffsetDateTime getOpenedOn() { return openedOn; }
        public OffsetDateTime getDueDate() { return dueDate; }
        public OffsetDateTime getClosedOn() { return closedOn; }
    }
}