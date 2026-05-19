package com.kryxhub.kryxhub.communication.repository;

import com.kryxhub.kryxhub.communication.entity.NotificationEntity;
import com.kryxhub.kryxhub.user.entity.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByUserOrderByCreatedAtDesc(UserEntity user);
    
    long countByUserAndIsReadFalse(UserEntity user);

    Page<NotificationEntity> findByUser(UserEntity user, Pageable pageable);
}