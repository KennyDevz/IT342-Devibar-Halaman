package edu.cit.devibar.halaman.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class MaintenanceLogResponse {

    private UUID maintenanceId;
    private String taskType;
    private String notes;
    private LocalDateTime completedAt;

    // Constructor mapping the exact fields we want to send to React
    public MaintenanceLogResponse(UUID maintenanceId, String taskType, String notes, LocalDateTime completedAt) {
        this.maintenanceId = maintenanceId;
        this.taskType = taskType;
        this.notes = notes;
        this.completedAt = completedAt;
    }

    // Getters
    public UUID getMaintenanceId() { return maintenanceId; }
    public String getTaskType() { return taskType; }
    public String getNotes() { return notes; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    // Setters
    public void setMaintenanceId(UUID maintenanceId) { this.maintenanceId = maintenanceId; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}