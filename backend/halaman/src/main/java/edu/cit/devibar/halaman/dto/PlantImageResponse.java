// In PlantImageResponse.java
package edu.cit.devibar.halaman.dto;

import java.time.LocalDateTime;

public class PlantImageResponse {
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private String caption;

    public PlantImageResponse(String fileUrl, LocalDateTime uploadedAt, String caption) {
        this.fileUrl = fileUrl;
        this.uploadedAt = uploadedAt;
        this.caption = caption;
    }

    // Getters
    public String getFileUrl() { return fileUrl; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public String getCaption() { return caption;}
}