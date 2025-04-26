package com.example.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String id;
    private String recipient;
    private String subject;
    private String content;
    private NotificationType type;
    private LocalDateTime timestamp;
    private boolean sent;
    
    public enum NotificationType {
        EMAIL, SMS, PUSH
    }
    
    // Constructor for creating a new notification
    public Notification(String recipient, String subject, String content, NotificationType type) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.sent = false;
    }
} 