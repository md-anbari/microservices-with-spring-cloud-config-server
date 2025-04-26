package com.example.profileservice.service;

import com.example.profileservice.model.Profile;
import com.example.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final NotificationClient notificationClient;

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Optional<Profile> getProfileById(Long id) {
        return profileRepository.findById(id);
    }

    public Profile createProfile(Profile profile) {
        Profile savedProfile = profileRepository.save(profile);
        
        try {
            // Send notification about profile creation
            notificationClient.sendProfileNotification(
                savedProfile.getId().toString(),
                savedProfile.getEmail(),
                "created"
            );
            log.info("Notification sent for profile creation: {}", savedProfile.getId());
        } catch (Exception e) {
            log.error("Failed to send notification for profile creation: {}", e.getMessage());
            // Continue anyway - don't fail the profile creation if notification fails
        }
        
        return savedProfile;
    }
} 