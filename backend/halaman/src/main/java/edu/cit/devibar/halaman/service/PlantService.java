package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.AuthResponse;
import edu.cit.devibar.halaman.dto.PlantRequest;
import edu.cit.devibar.halaman.dto.PlantResponse;
import edu.cit.devibar.halaman.entity.CareSchedule;
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

    public AuthResponse getAllPlants(UUID userId) {
        List<PlantResponse> plants = plantRepository
                .findByUserUserIdAndDeletedAtIsNull(userId)
                .stream()
                // FIX: Map each plant by fetching its schedule date first
                .map(plant -> {
                    LocalDateTime dueDate = careScheduleService.getWateringScheduleForPlant(plant.getPlantId())
                            .map(schedule -> schedule.getNextDueDate().atStartOfDay())
                            .orElse(null);
                    return PlantResponse.fromEntity(plant, dueDate);
                })
                .collect(Collectors.toList());

        AuthResponse.DataPayload payload = new AuthResponse.DataPayload();
        payload.setPlants(plants);
        return AuthResponse.success(payload);
    }

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

    // 1. GET RECYCLE BIN
    public AuthResponse getDeletedPlants(UUID userId) {
        List<PlantResponse> trashedPlants = plantRepository
                .findByUserUserIdAndDeletedAtIsNotNull(userId)
                .stream()
                // We pass null for the schedule date because trashed plants shouldn't have active schedules
                .map(plant -> PlantResponse.fromEntity(plant, null))
                .collect(Collectors.toList());

        AuthResponse.DataPayload payload = new AuthResponse.DataPayload();
        payload.setPlants(trashedPlants);
        return AuthResponse.success(payload);
    }

    // 2. RESTORE PLANT
    @Transactional
    public AuthResponse restorePlant(UUID plantId, UUID userId) {
        Plant plant = plantRepository.findById(plantId).orElse(null);

        if (plant == null || plant.getDeletedAt() == null) {
            return AuthResponse.error("DB-002", "Plant not found", "This plant does not exist in the recycle bin.");
        }

        if (!plant.getUser().getUserId().equals(userId)) {
            return AuthResponse.error("AUTH-003", "Forbidden", "You do not have access to restore this plant.");
        }

        // Use the facade to restore schedules and images
        plantDeletionFacade.executeRestore(plant);
        plantRepository.save(plant);

        return AuthResponse.success(null);
    }

    // 3. PERMANENT DELETE
    @Transactional
    public AuthResponse permanentlyDeletePlant(UUID plantId, UUID userId) {
        Plant plant = plantRepository.findById(plantId).orElse(null);

        if (plant == null || plant.getDeletedAt() == null) {
            return AuthResponse.error("DB-002", "Plant not found", "This plant does not exist in the recycle bin.");
        }

        if (!plant.getUser().getUserId().equals(userId)) {
            return AuthResponse.error("AUTH-003", "Forbidden", "You do not have access to delete this plant.");
        }

        // Use the facade to delete physical files from Cloudinary
        plantDeletionFacade.executePermanentDelete(plant);

        // Wipe the row from the database
        plantRepository.delete(plant);

        return AuthResponse.success(null);
    }

    private AuthResponse.DataPayload buildDataPayload(Plant plant) {
        LocalDateTime dueDate = careScheduleService.getWateringScheduleForPlant(plant.getPlantId())
                .map(schedule -> schedule.getNextDueDate().atStartOfDay())
                .orElse(null);

        AuthResponse.DataPayload payload = new AuthResponse.DataPayload();
        payload.setPlant(PlantResponse.fromEntity(plant, dueDate));
        return payload;
    }
}