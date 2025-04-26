package com.example.notificationservice.service;

import com.example.notificationservice.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ConcurrentHashMap<String, Notification> notifications = new ConcurrentHashMap<>();
    
    @Value("${app.notification-service.simulate-sending:true}")
    private boolean simulateSending;
    
    public Notification sendNotification(Notification notification) {
        // Generate an ID if not present
        if (notification.getId() == null) {
            notification.setId(UUID.randomUUID().toString());
        }
        
        log.info("Sending notification: [type={}, recipient={}, subject={}]", 
                notification.getType(), notification.getRecipient(), notification.getSubject());
        
        // In a real service, we would send the notification through an email/SMS provider
        // Here we'll just simulate sending
        if (simulateSending) {
            try {
                // Simulate some processing time
                Thread.sleep(500);
                notification.setSent(true);
                log.info("Notification sent successfully: {}", notification.getId());
            } catch (InterruptedException e) {
                log.error("Error while sending notification", e);
                Thread.currentThread().interrupt();
            }
        }
        
        // Store the notification
        notifications.put(notification.getId(), notification);
        return notification;
    }
    
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications.values());
    }
    
    public Notification getNotificationById(String id) {
        return notifications.get(id);
    }
} 