package com.kryxhub.kryxhub.communication.repository;

import com.kryxhub.kryxhub.communication.entity.SupportTicketEntity;
import com.kryxhub.kryxhub.user.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicketEntity, UUID> {

    Page<SupportTicketEntity> findByUserOrderByOpenedOnDesc(UserEntity user, Pageable pageable);
}
