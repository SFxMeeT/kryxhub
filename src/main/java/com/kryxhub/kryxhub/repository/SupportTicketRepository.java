package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.SupportTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kryxhub.kryxhub.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicketEntity, UUID> {

    Page<SupportTicketEntity> findByUserOrderByOpenedOnDesc(UserEntity user, Pageable pageable);
}
