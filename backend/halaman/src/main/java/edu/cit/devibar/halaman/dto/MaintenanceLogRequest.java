package edu.cit.devibar.halaman.dto;

import java.util.UUID;

public class MaintenanceLogRequest {
    private UUID plantId;
    private String taskType = "WATERING"; // Default to watering for Phase 3
    private String notes;

    public UUID getPlantId() { return plantId; }
    public void setPlantId(UUID plantId) { this.plantId = plantId; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}