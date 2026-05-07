package edu.cit.devibar.halaman.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_logs")
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID logId;

    // e.g., "REGISTER", "DELETE_PLANT", "SUSPEND_USER"
    @Column(name = "action_type", nullable = false)
    private String actionType;

    // A human-readable description: "Admin John suspended user Alice"
    @Column(nullable = false, length = 500)
    private String description;

    // Optional: Who performed the action?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters
    public UUID getLogId() { return logId; }
    public String getActionType() { return actionType; }
    public String getDescription() { return description; }
    public User getActor() { return actor; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setLogId(UUID logId) { this.logId = logId; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public void setDescription(String description) { this.description = description; }
    public void setActor(User actor) { this.actor = actor; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}