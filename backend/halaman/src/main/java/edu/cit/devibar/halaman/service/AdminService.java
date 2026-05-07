package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.ActivityLogDto;
import edu.cit.devibar.halaman.dto.ImageModerationDto;
import edu.cit.devibar.halaman.entity.PlantImage;
import edu.cit.devibar.halaman.entity.SystemLog;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.repository.PlantImageRepository;
import edu.cit.devibar.halaman.repository.PlantRepository;
import edu.cit.devibar.halaman.repository.SystemLogRepository;
import edu.cit.devibar.halaman.repository.UserRepository;
import org.springframework.stereotype.Service;
import edu.cit.devibar.halaman.service.storage.ImageStorageAdapter;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PlantRepository plantRepository;
    private final PlantImageRepository imageRepository;
    private final SystemLogRepository systemLogRepository;
    private final AuditService auditService;
    private final ImageStorageAdapter storageAdapter;

    public AdminService(UserRepository userRepository, PlantRepository plantRepository, PlantImageRepository imageRepository, SystemLogRepository systemLogRepository, AuditService auditService, ImageStorageAdapter storageAdapter) {
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
        this.imageRepository = imageRepository;
        this.systemLogRepository = systemLogRepository;
        this.auditService = auditService;
        this.storageAdapter = storageAdapter;
    }

    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalUsers", userRepository.count());
        metrics.put("totalPlants", plantRepository.count());
        metrics.put("totalImages", imageRepository.count());
        metrics.put("activeUsers", userRepository.count());
        return metrics;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User toggleUserStatus(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(user.getStatus().equals("ACTIVE") ? "SUSPENDED" : "ACTIVE");
        return userRepository.save(user);
    }

    public List<ActivityLogDto> getRecentActivity() {
        // Fetch the real logs from the database
        List<SystemLog> logs = systemLogRepository.findTop5ByOrderByCreatedAtDesc();

        // Map them to DTOs for the frontend
        return logs.stream().map(log -> new ActivityLogDto(
                log.getLogId().toString(),
                log.getActionType(),
                log.getDescription(),
                log.getCreatedAt()
        )).collect(Collectors.toList());
    }

    public List<ImageModerationDto> getAllModerationImages() {
        List<PlantImage> images = imageRepository.findAll(); // Or findByDeletedAtIsNull() if you soft delete

        return images.stream().map(img -> new ImageModerationDto(
                img.getImageId().toString(),
                img.getFileUrl(),
                img.getPlant().getUser().getFirstName() + " " + img.getPlant().getUser().getLastName(),
                img.getPlant().getNickname(),
                img.getUploadedAt()
        )).collect(Collectors.toList());
    }

    public void deleteImage(UUID imageId) {
        PlantImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // STEP A: Delete the physical file from Cloudinary using your adapter!
        storageAdapter.deleteImage(image.getFileUrl());

        // STEP B: Prepare the log description
        String logDescription = "Admin deleted an image belonging to plant: " + image.getPlant().getNickname();

        // STEP C: Delete the record from your Supabase Database
        imageRepository.delete(image);

        // STEP D: Record the action in the Audit Log
        auditService.logAction("IMAGE_DELETE", logDescription, null);
    }
}