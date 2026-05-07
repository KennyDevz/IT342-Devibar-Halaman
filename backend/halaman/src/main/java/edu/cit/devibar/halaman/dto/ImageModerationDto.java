package edu.cit.devibar.halaman.dto;

import java.time.LocalDateTime;

public class ImageModerationDto {
    private String id;
    private String imageUrl;
    private String uploaderName;
    private String plantName;
    private LocalDateTime uploadedAt;

    public ImageModerationDto(String id, String imageUrl, String uploaderName, String plantName, LocalDateTime uploadedAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.uploaderName = uploaderName;
        this.plantName = plantName;
        this.uploadedAt = uploadedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public String getUploaderName() { return uploaderName; }
    public String getPlantName() { return plantName; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}