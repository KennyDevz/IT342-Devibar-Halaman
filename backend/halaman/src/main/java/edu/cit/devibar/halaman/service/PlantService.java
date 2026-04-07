package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.AuthResponse;
import edu.cit.devibar.halaman.dto.PlantRequest;
import edu.cit.devibar.halaman.dto.PlantResponse;
import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.repository.PlantRepository;
import edu.cit.devibar.halaman.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final CareScheduleService careScheduleService;
    private final PlantDeletionFacade plantDeletionFacade;

    public PlantService(PlantRepository plantRepository,
                        UserRepository userRepository, CareScheduleService careScheduleService, PlantDeletionFacade plantDeletionFacade) {
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.careScheduleService = careScheduleService;
        this.plantDeletionFacade = plantDeletionFacade;
    }

    // ==========================================
    // GET ALL PLANTS
    // ==========================================
    public AuthResponse getAllPlants(UUID userId) {
        List<PlantResponse> plants = plantRepository
                .findByUserUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(PlantResponse::fromEntity)
                .collect(Collectors.toList());

        AuthResponse.DataPayload payload = new AuthResponse.DataPayload();
        payload.setPlants(plants);
        return AuthResponse.success(payload);
    }

    // ==========================================
    // GET SINGLE PLANT
    // ==========================================
    public AuthResponse getPlant(UUID plantId, UUID userId) {
        var plant = plantRepository
                .findByPlantIdAndDeletedAtIsNull(plantId)
                .orElse(null);

        if (plant == null) {
            return AuthResponse.error(
                    "DB-001",
                    "Plant not found",
                    "No plant found with this ID"
            );
        }

        // Make sure plant belongs to this user
        if (!plant.getUser().getUserId().equals(userId)) {
            return AuthResponse.error(
                    "AUTH-003",
                    "Forbidden",
                    "You do not have access to this plant"
            );
        }

        return AuthResponse.success(buildDataPayload(plant));
    }

    // ==========================================
    // CREATE PLANT
    // ==========================================
    @Transactional
    public AuthResponse createPlant(PlantRequest request, UUID userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return AuthResponse.error(
                    "DB-001",
                    "User not found",
                    "No user found with this ID"
            );
        }

        // Check duplicate nickname
        if (plantRepository.existsByUserUserIdAndNicknameAndDeletedAtIsNull(
                userId, request.getNickname())) {
            return AuthResponse.error(
                    "DB-002",
                    "Duplicate plant nickname",
                    "You already have a plant with this nickname"
            );
        }

        Plant plant = new Plant();
        plant.setUser(user);
        plant.setNickname(request.getNickname());
        plant.setSpeciesName(request.getSpeciesName());
        plant.setWateringFrequencyDays(request.getWateringFrequencyDays());

        Plant savedPlant = plantRepository.save(plant);

        careScheduleService.createInitialSchedule(savedPlant, request.getWateringFrequencyDays());

        return AuthResponse.success(buildDataPayload(savedPlant));
    }

    // ==========================================
    // UPDATE PLANT
    // ==========================================
    @Transactional
    public AuthResponse updatePlant(UUID plantId,
                                    PlantRequest request,
                                    UUID userId) {
        Plant plant = plantRepository
                .findByPlantIdAndDeletedAtIsNull(plantId)
                .orElse(null);

        if (plant == null) {
            return AuthResponse.error(
                    "DB-001",
                    "Plant not found",
                    "No plant found with this ID"
            );
        }

        // Make sure plant belongs to this user
        if (!plant.getUser().getUserId().equals(userId)) {
            return AuthResponse.error(
                    "AUTH-003",
                    "Forbidden",
                    "You do not have access to this plant"
            );
        }

        plant.setNickname(request.getNickname());
        plant.setSpeciesName(request.getSpeciesName());
        plant.setWateringFrequencyDays(request.getWateringFrequencyDays());

        plantRepository.save(plant);

        return AuthResponse.success(buildDataPayload(plant));
    }

    // ==========================================
    // DELETE PLANT (Enhanced Soft Delete via Facade)
    // ==========================================
    @Transactional
    public AuthResponse deletePlant(UUID plantId, UUID userId) {
        Plant plant = plantRepository
                .findByPlantIdAndDeletedAtIsNull(plantId)
                .orElse(null);

        if (plant == null) {
            return AuthResponse.error("DB-001", "Plant not found", "No plant found with this ID");
        }

        if (!plant.getUser().getUserId().equals(userId)) {
            return AuthResponse.error("AUTH-003", "Forbidden", "You do not have access to this plant");
        }

        // REFACTORED: Delegate the entire complex orchestration to the Facade
        plantDeletionFacade.executeSoftDelete(plant);

        plantRepository.save(plant);

        return AuthResponse.success(null);
    }

    // ==========================================
    // PRIVATE HELPER
    // ==========================================
    private AuthResponse.DataPayload buildDataPayload(Plant plant) {
        AuthResponse.DataPayload payload = new AuthResponse.DataPayload();
        payload.setPlant(PlantResponse.fromEntity(plant));
        return payload;
    }
}