package edu.cit.devibar.halaman.features.plant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.cit.devibar.halaman.features.auth.AuthResponse;
import edu.cit.devibar.halaman.features.auth.User;
import edu.cit.devibar.halaman.features.auth.UserRepository;
import edu.cit.devibar.halaman.features.careschedule.CareScheduleService;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Collections;
import java.time.LocalDate;
import edu.cit.devibar.halaman.features.careschedule.CareSchedule;

@ExtendWith(MockitoExtension.class)
public class PlantServiceTest {

    // 1. We must mock ALL the dependencies your actual PlantService uses
    @Mock
    private PlantRepository plantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CareScheduleService careScheduleService;

    @Mock
    private PlantDeletionFacade plantDeletionFacade;

    @InjectMocks
    private PlantService plantService;

    private Plant mockPlant;
    private UUID mockPlantId;
    private User mockUser;
    private UUID mockUserId;

    @BeforeEach
    void setUp() {
        mockPlantId = UUID.randomUUID();
        mockUserId = UUID.randomUUID();

        // Create a fake user
        mockUser = new User();
        mockUser.setUserId(mockUserId);

        // Create a fake plant and attach it to the fake user
        mockPlant = new Plant();
        mockPlant.setPlantId(mockPlantId);
        mockPlant.setNickname("Monstera Deliciosa");
        mockPlant.setSpeciesName("Monstera");
        mockPlant.setUser(mockUser);
    }

    @Test
    void createPlant_ShouldSavePlantAndCreateSchedule() {
        // Arrange
        PlantRequest request = new PlantRequest();
        request.setNickname("Monstera Deliciosa");
        request.setSpeciesName("Monstera");
        request.setWateringFrequencyDays(7);

        // Tell our mocks how to behave when the service calls them
        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
        when(plantRepository.existsByUserUserIdAndNicknameAndDeletedAtIsNull(mockUserId, "Monstera Deliciosa"))
                .thenReturn(false);
        when(plantRepository.save(any(Plant.class))).thenReturn(mockPlant);

        // Act - Now we pass BOTH the request and the userId!
        AuthResponse response = plantService.createPlant(request, mockUserId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());

        // Verify the repository saved the plant
        verify(plantRepository, times(1)).save(any(Plant.class));

        // Verify the care schedule service was called to create the initial schedule!
        verify(careScheduleService, times(1)).createInitialSchedule(mockPlant, 7);
    }

    @Test
    void deletePlant_ShouldCallFacadeAndSave() {
        // Arrange
        when(plantRepository.findByPlantIdAndDeletedAtIsNull(mockPlantId))
                .thenReturn(Optional.of(mockPlant));

        // Act - We use deletePlant and pass the userId
        AuthResponse response = plantService.deletePlant(mockPlantId, mockUserId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());

        // Because the Facade handles the soft delete logic now, we just verify the Facade was called!
        verify(plantDeletionFacade, times(1)).executeSoftDelete(mockPlant);

        // Verify it was saved after the facade modified it
        verify(plantRepository, times(1)).save(mockPlant);
    }

    @Test
    void getAllPlants_ShouldReturnListOfPlants() {
        // Arrange
        when(plantRepository.findByUserUserIdAndDeletedAtIsNull(mockUserId))
                .thenReturn(Collections.singletonList(mockPlant));
        
        CareSchedule mockSchedule = new CareSchedule();
        mockSchedule.setNextDueDate(LocalDate.now());
        when(careScheduleService.getWateringScheduleForPlant(mockPlantId))
                .thenReturn(Optional.of(mockSchedule));

        // Act
        AuthResponse response = plantService.getAllPlants(mockUserId);

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getPlants());
        assertEquals(1, response.getData().getPlants().size());
    }

    @Test
    void getPlant_ShouldReturnPlant_WhenValid() {
        when(plantRepository.findByPlantIdAndDeletedAtIsNull(mockPlantId))
                .thenReturn(Optional.of(mockPlant));
        when(careScheduleService.getWateringScheduleForPlant(mockPlantId))
                .thenReturn(Optional.empty());

        AuthResponse response = plantService.getPlant(mockPlantId, mockUserId);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getPlant());
        assertEquals(mockPlantId, response.getData().getPlant().getPlantId());
    }

    @Test
    void getPlant_ShouldReturnError_WhenNotFound() {
        when(plantRepository.findByPlantIdAndDeletedAtIsNull(mockPlantId))
                .thenReturn(Optional.empty());

        AuthResponse response = plantService.getPlant(mockPlantId, mockUserId);

        assertFalse(response.isSuccess());
        assertEquals("DB-001", response.getError().getCode());
    }

    @Test
    void getPlant_ShouldReturnError_WhenForbidden() {
        User wrongUser = new User();
        wrongUser.setUserId(UUID.randomUUID());
        mockPlant.setUser(wrongUser);

        when(plantRepository.findByPlantIdAndDeletedAtIsNull(mockPlantId))
                .thenReturn(Optional.of(mockPlant));

        AuthResponse response = plantService.getPlant(mockPlantId, mockUserId);

        assertFalse(response.isSuccess());
        assertEquals("AUTH-003", response.getError().getCode());
    }

    @Test
    void createPlant_ShouldReturnError_WhenUserNotFound() {
        PlantRequest request = new PlantRequest();
        when(userRepository.findById(mockUserId)).thenReturn(Optional.empty());

        AuthResponse response = plantService.createPlant(request, mockUserId);

        assertFalse(response.isSuccess());
        assertEquals("DB-001", response.getError().getCode());
    }

    @Test
    void createPlant_ShouldReturnError_WhenDuplicateNickname() {
        PlantRequest request = new PlantRequest();
        request.setNickname("Monstera Deliciosa");
        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
        when(plantRepository.existsByUserUserIdAndNicknameAndDeletedAtIsNull(mockUserId, "Monstera Deliciosa"))
                .thenReturn(true);

        AuthResponse response = plantService.createPlant(request, mockUserId);

        assertFalse(response.isSuccess());
        assertEquals("DB-002", response.getError().getCode());
    }

    @Test
    void updatePlant_ShouldUpdatePlantAndSchedule_WhenFrequencyChanges() {
        PlantRequest request = new PlantRequest();
        request.setWateringFrequencyDays(10);
        
        mockPlant.setWateringFrequencyDays(7);
        CareSchedule schedule = new CareSchedule();
        schedule.setTaskType("WATERING");
        mockPlant.setCareSchedules(Collections.singletonList(schedule));

        when(plantRepository.findByPlantIdAndDeletedAtIsNull(mockPlantId))
                .thenReturn(Optional.of(mockPlant));
        when(plantRepository.save(any(Plant.class))).thenReturn(mockPlant);

        AuthResponse response = plantService.updatePlant(mockPlantId, request, mockUserId);

        assertTrue(response.isSuccess());
        assertEquals(10, schedule.getFrequencyDays());
        verify(plantRepository, times(1)).save(mockPlant);
    }

    @Test
    void updatePlant_ShouldReturnError_WhenNotFound() {
        PlantRequest request = new PlantRequest();
        when(plantRepository.findByPlantIdAndDeletedAtIsNull(mockPlantId))
                .thenReturn(Optional.empty());

        AuthResponse response = plantService.updatePlant(mockPlantId, request, mockUserId);

        assertFalse(response.isSuccess());
        assertEquals("DB-001", response.getError().getCode());
    }

    @Test
    void deletePlant_ShouldReturnError_WhenForbidden() {
        User wrongUser = new User();
        wrongUser.setUserId(UUID.randomUUID());
        mockPlant.setUser(wrongUser);

        when(plantRepository.findByPlantIdAndDeletedAtIsNull(mockPlantId))
                .thenReturn(Optional.of(mockPlant));

        AuthResponse response = plantService.deletePlant(mockPlantId, mockUserId);

        assertFalse(response.isSuccess());
        assertEquals("AUTH-003", response.getError().getCode());
        verify(plantDeletionFacade, never()).executeSoftDelete(any(Plant.class));
    }

    @Test
    void getDeletedPlants_ShouldReturnTrashedPlants() {
        when(plantRepository.findByUserUserIdAndDeletedAtIsNotNull(mockUserId))
                .thenReturn(Collections.singletonList(mockPlant));

        AuthResponse response = plantService.getDeletedPlants(mockUserId);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getPlants());
        assertEquals(1, response.getData().getPlants().size());
    }

    @Test
    void restorePlant_ShouldRestore_WhenValid() {
        mockPlant.setDeletedAt(java.time.LocalDateTime.now());
        when(plantRepository.findById(mockPlantId)).thenReturn(Optional.of(mockPlant));

        AuthResponse response = plantService.restorePlant(mockPlantId, mockUserId);

        assertTrue(response.isSuccess());
        verify(plantDeletionFacade, times(1)).executeRestore(mockPlant);
        verify(plantRepository, times(1)).save(mockPlant);
    }

    @Test
    void restorePlant_ShouldReturnError_WhenNotInRecycleBin() {
        mockPlant.setDeletedAt(null);
        when(plantRepository.findById(mockPlantId)).thenReturn(Optional.of(mockPlant));

        AuthResponse response = plantService.restorePlant(mockPlantId, mockUserId);

        assertFalse(response.isSuccess());
        assertEquals("DB-002", response.getError().getCode());
    }

    @Test
    void permanentlyDeletePlant_ShouldDelete_WhenValid() {
        mockPlant.setDeletedAt(java.time.LocalDateTime.now());
        when(plantRepository.findById(mockPlantId)).thenReturn(Optional.of(mockPlant));

        AuthResponse response = plantService.permanentlyDeletePlant(mockPlantId, mockUserId);

        assertTrue(response.isSuccess());
        verify(plantDeletionFacade, times(1)).executePermanentDelete(mockPlant);
        verify(plantRepository, times(1)).delete(mockPlant);
    }

    @Test
    void permanentlyDeletePlant_ShouldReturnError_WhenForbidden() {
        mockPlant.setDeletedAt(java.time.LocalDateTime.now());
        User wrongUser = new User();
        wrongUser.setUserId(UUID.randomUUID());
        mockPlant.setUser(wrongUser);
        
        when(plantRepository.findById(mockPlantId)).thenReturn(Optional.of(mockPlant));

        AuthResponse response = plantService.permanentlyDeletePlant(mockPlantId, mockUserId);

        assertFalse(response.isSuccess());
        assertEquals("AUTH-003", response.getError().getCode());
    }
}