package edu.cit.devibar.halaman.dto;

import edu.cit.devibar.halaman.entity.Plant;
import java.time.LocalDateTime;
import java.util.UUID;

public class PlantResponse {

    private final UUID plantId;
    private final String nickname;
    private final String speciesName;
    private final Integer wateringFrequencyDays;
    private final LocalDateTime createdAt;

    // Private constructor forces the use of the Builder
    private PlantResponse(Builder builder) {
        this.plantId = builder.plantId;
        this.nickname = builder.nickname;
        this.speciesName = builder.speciesName;
        this.wateringFrequencyDays = builder.wateringFrequencyDays;
        this.createdAt = builder.createdAt;
    }

    // Getters only - No setters! The object is now strictly immutable.
    public UUID getPlantId(){ return plantId; }
    public String getNickname(){ return nickname; }
    public String getSpeciesName(){ return speciesName; }
    public Integer getWateringFrequencyDays(){ return wateringFrequencyDays; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    // The Builder static class
    public static class Builder {
        private UUID plantId;
        private String nickname;
        private String speciesName;
        private Integer wateringFrequencyDays;
        private LocalDateTime createdAt;

        public Builder plantId(UUID plantId) { this.plantId = plantId; return this; }
        public Builder nickname(String nickname) { this.nickname = nickname; return this; }
        public Builder speciesName(String speciesName) { this.speciesName = speciesName; return this; }
        public Builder wateringFrequencyDays(Integer days) { this.wateringFrequencyDays = days; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PlantResponse build() {
            return new PlantResponse(this);
        }
    }

    // Updated fromEntity mapping method using the Builder
    public static PlantResponse fromEntity(Plant plant) {
        return new Builder()
                .plantId(plant.getPlantId())
                .nickname(plant.getNickname())
                .speciesName(plant.getSpeciesName())
                .wateringFrequencyDays(plant.getWateringFrequencyDays())
                .createdAt(plant.getCreatedAt())
                .build();
    }
}