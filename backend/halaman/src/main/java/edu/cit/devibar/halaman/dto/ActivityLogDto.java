package edu.cit.devibar.halaman.dto;

import java.time.LocalDateTime;

public class ActivityLogDto {
    private String id;
    private String type;
    private String text;
    private LocalDateTime timestamp;

    public ActivityLogDto(String id, String type, String text, LocalDateTime timestamp) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getText() { return text; }
    public LocalDateTime getTimestamp() { return timestamp; }
}