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
}