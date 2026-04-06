package edu.cit.devibar.halaman.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PlantRequest {

    @NotBlank(message = "Nickname is required")
    private String nickname;

    @NotBlank(message = "Species name is required")
    private String speciesName;

    @NotNull(message = "Watering frequency is required")
    @Min(value = 1, message = "Watering frequency must be at least 1 day")
    private Integer wateringFrequencyDays;

    // Getters
    public String getNickname(){ return nickname; }
    public String getSpeciesName(){ return speciesName; }
    public Integer getWateringFrequencyDays(){ return wateringFrequencyDays; }

    // Setters
    public void setNickname(String nickname){ this.nickname = nickname; }
    public void setSpeciesName(String speciesName){ this.speciesName = speciesName; }
    public void setWateringFrequencyDays(Integer days){ this.wateringFrequencyDays = days; }
}