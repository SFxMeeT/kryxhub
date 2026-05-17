package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.entity.NotificationEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.repository.NotificationRepository;
import com.kryxhub.kryxhub.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createAndSend(UserEntity user, String title, String message, String type, String actionUrl) {
        
        if (Boolean.FALSE.equals(user.getPopupNotifications())) {
            System.out.println("Skipping notification for " + user.getEmail() + " (Notifications Disabled)");
            return;
        }

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type); 
        notification.setActionUrl(actionUrl);

        notificationRepository.save(notification);
    }

    public Page<NotificationEntity> getUserNotifications(String email, int page, int size) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByUser(user, pageable); // Make sure this exists in your Repo!
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}