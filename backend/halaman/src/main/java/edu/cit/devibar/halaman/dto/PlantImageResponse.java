// In PlantImageResponse.java
package edu.cit.devibar.halaman.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlantImageResponse {
    private UUID imageId;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private String caption;

    public PlantImageResponse(UUID imageId, String  fileUrl, LocalDateTime uploadedAt, String caption) {
        this.imageId = imageId;
        this.fileUrl = fileUrl;
        this.uploadedAt = uploadedAt;
        this.caption = caption;
    }

    // Getters
    public UUID getImageId() { return  imageId; }
    public String getFileUrl() { return fileUrl; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public String getCaption() { return caption;}
}