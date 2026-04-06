package edu.cit.devibar.halaman.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "plant_images")
public class PlantImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "image_id")
    private UUID imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(length = 255)
    private String caption;

    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    //Setters
    public void setImageId(UUID imageId) { this.imageId = imageId; }
    public void setPlant(Plant plant) { this.plant = plant; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setCaption(String caption) { this.caption = caption; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    //Getters
    public UUID getImageId() { return imageId; }
    public Plant getPlant() { return plant; }
    public String getFileUrl() { return fileUrl; }
    public String getCaption() { return caption; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

}