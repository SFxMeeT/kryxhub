package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.SupportTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<SupportTicketEntity, UUID> {
}
