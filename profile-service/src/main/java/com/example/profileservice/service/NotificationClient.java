package com.example.profileservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service", url = "http://localhost:8083")
public interface NotificationClient {

    @PostMapping("/api/notifications/user-event")
    ResponseEntity<Object> sendProfileNotification(
            @RequestParam("userId") String userId,
            @RequestParam("email") String email,
            @RequestParam("action") String action
    );
} 