package com.kryxhub.kryxhub.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.kryxhub.kryxhub.entity.NotificationEntity;

public class NotificationDto {
    public UUID id;
    public String title;
    public String message;
    public String type;
    public String actionUrl;
    public Boolean isRead;
    public OffsetDateTime createdAt;

    public NotificationDto(NotificationEntity n) {
        this.id = n.getId();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.type = n.getType();
        this.actionUrl = n.getActionUrl();
        this.isRead = n.getRead();
        this.createdAt = n.getCreatedAt();
    }
}
