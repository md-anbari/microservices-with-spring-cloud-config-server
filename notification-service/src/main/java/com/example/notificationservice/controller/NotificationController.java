package com.example.notificationservice.controller;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    
    @PostMapping("/user-event")
    public ResponseEntity<Notification> sendUserEventNotification(
            @RequestParam String userId,
            @RequestParam String email,
            @RequestParam String action) {
        
        String subject = "User " + action + " notification";
        String content = "Your account has been " + action + ". User ID: " + userId;
        
        Notification notification = new Notification(
                email,
                subject,
                content,
                Notification.NotificationType.EMAIL
        );
        
        Notification sent = notificationService.sendNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(sent);
    }
} 