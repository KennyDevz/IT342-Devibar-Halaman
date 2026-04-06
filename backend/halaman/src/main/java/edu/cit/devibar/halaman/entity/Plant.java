package edu.cit.devibar.halaman.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "plants")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "plant_id")
    private UUID plantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "species_name", nullable = false, length = 255)
    private String speciesName;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(name = "watering_frequency_days")
    private Integer wateringFrequencyDays;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MaintenanceLog> maintenanceLogs = new ArrayList<>();

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CareSchedule> careSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PlantImage> images = new ArrayList<>();

    // Getters
    public UUID getPlantId() { return plantId; }
    public User getUser(){ return user; }
    public String getSpeciesName() { return speciesName; }
    public String getNickname() { return nickname; }
    public Integer getWateringFrequencyDays() { return wateringFrequencyDays; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public LocalDateTime getDeletedAt(){ return deletedAt; }
    public List<MaintenanceLog> getMaintenanceLogs() { return maintenanceLogs; }
    public List<CareSchedule> getCareSchedules() { return careSchedules; }
    public List<PlantImage> getImages() { return images; }

    // Setters
    public void setPlantId(UUID plantId) { this.plantId = plantId; }
    public void setUser(User user) { this.user = user; }
    public void setSpeciesName(String speciesName)  { this.speciesName = speciesName; }
    public void setNickname(String nickname){ this.nickname = nickname; }
    public void setWateringFrequencyDays(Integer days){ this.wateringFrequencyDays = days; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void setMaintenanceLogs(List<MaintenanceLog> logs) { this.maintenanceLogs = logs; }
    public void setCareSchedules(List<CareSchedule> schedules) { this.careSchedules = schedules; }
    public void setImages(List<PlantImage> images) { this.images = images; }
}