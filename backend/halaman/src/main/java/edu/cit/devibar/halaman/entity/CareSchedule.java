package edu.cit.devibar.halaman.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "care_schedules")
public class CareSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "care_id")
    private UUID careId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Column(name = "task_type", nullable = false)
    private String taskType = "WATERING";

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "frequency_days")
    private Integer frequencyDays;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Getters
    public UUID getCareId() { return careId; }
    public Plant getPlant() { return plant; }
    public String getTaskType() { return taskType; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public Integer getFrequencyDays() { return frequencyDays; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

    // Setters
    public void setCareId(UUID careId) { this.careId = careId; }
    public void setPlant(Plant plant) { this.plant = plant; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }
    public void setFrequencyDays(Integer frequencyDays) { this.frequencyDays = frequencyDays; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}