package edu.cit.devibar.halaman.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "maintenance_logs")
public class MaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "maintenance_id")
    private UUID maintenanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Column(name = "task_type", nullable = false)
    private String taskType;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(length = 500)
    private String notes;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.completedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getMaintenanceId() { return maintenanceId; }
    public Plant getPlant() { return plant; }
    public String getTaskType() { return taskType; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getNotes() { return notes; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

    // Setters
    public void setMaintenanceId(UUID maintenanceId) { this.maintenanceId = maintenanceId; }
    public void setPlant(Plant plant) { this.plant = plant; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}