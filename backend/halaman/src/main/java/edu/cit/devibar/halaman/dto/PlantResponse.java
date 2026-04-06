package edu.cit.devibar.halaman.dto;

import edu.cit.devibar.halaman.entity.Plant;
import java.time.LocalDateTime;
import java.util.UUID;

public class PlantResponse {

    private UUID plantId;
    private String nickname;
    private String speciesName;
    private Integer wateringFrequencyDays;
    private LocalDateTime createdAt;

    // Getters
    public UUID getPlantId(){ return plantId; }
    public String getNickname(){ return nickname; }
    public String getSpeciesName(){ return speciesName; }
    public Integer getWateringFrequencyDays(){ return wateringFrequencyDays; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    // Setters
    public void setPlantId(UUID plantId){ this.plantId = plantId; }
    public void setNickname(String nickname){ this.nickname = nickname; }
    public void setSpeciesName(String speciesName){ this.speciesName = speciesName; }
    public void setWateringFrequencyDays(Integer days){ this.wateringFrequencyDays = days; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }

    public static PlantResponse fromEntity(Plant plant) {
        PlantResponse response = new PlantResponse();
        response.setPlantId(plant.getPlantId());
        response.setNickname(plant.getNickname());
        response.setSpeciesName(plant.getSpeciesName());
        response.setWateringFrequencyDays(plant.getWateringFrequencyDays());
        response.setCreatedAt(plant.getCreatedAt());
        return response;
    }
}