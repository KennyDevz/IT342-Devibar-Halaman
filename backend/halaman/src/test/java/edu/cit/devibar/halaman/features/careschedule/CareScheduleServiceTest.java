package edu.cit.devibar.halaman.features.careschedule;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.cit.devibar.halaman.features.plant.Plant;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CareScheduleServiceTest {

    // Mock the specific repository we know your service uses
    @Mock
    private CareScheduleRepository careScheduleRepository;

    @InjectMocks
    private CareScheduleService careScheduleService;

    private UUID mockPlantId;
    private Plant mockPlant;

    @BeforeEach
    void setUp() {
        mockPlantId = UUID.randomUUID();
        mockPlant = new Plant();
        mockPlant.setPlantId(mockPlantId);
    }

    @Test
    void createInitialSchedule_ShouldSaveNewWateringSchedule() {
        // Arrange
        int frequencyDays = 7;
        CareSchedule savedSchedule = new CareSchedule();
        savedSchedule.setPlant(mockPlant); // Assuming your entity uses the Plant object directly
        savedSchedule.setTaskType("WATERING");
        savedSchedule.setFrequencyDays(frequencyDays);
        savedSchedule.setNextDueDate(LocalDate.now().plusDays(frequencyDays));

        when(careScheduleRepository.save(any(CareSchedule.class))).thenReturn(savedSchedule);

        // Act
        careScheduleService.createInitialSchedule(mockPlant, frequencyDays);

        // Assert
        // Verify that the service correctly commanded the repository to save the new schedule
        verify(careScheduleRepository, times(1)).save(any(CareSchedule.class));
    }

    @Test
    void getWateringScheduleForPlant_ShouldReturnOptionalSchedule() {
        // Arrange
        CareSchedule mockSchedule = new CareSchedule();
        mockSchedule.setTaskType("WATERING");

        // We use the EXACT repository method name from your earlier snippet!
        when(careScheduleRepository.findFirstByPlantPlantIdAndTaskTypeAndDeletedAtIsNull(mockPlantId, "WATERING"))
                .thenReturn(Optional.of(mockSchedule));

        // Act
        Optional<CareSchedule> result = careScheduleService.getWateringScheduleForPlant(mockPlantId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("WATERING", result.get().getTaskType());

        // Verify it queried the database securely ignoring soft-deleted items
        verify(careScheduleRepository, times(1))
                .findFirstByPlantPlantIdAndTaskTypeAndDeletedAtIsNull(mockPlantId, "WATERING");
    }
}