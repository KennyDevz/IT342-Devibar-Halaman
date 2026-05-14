package edu.cit.devibar.halaman.features.maintenance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import edu.cit.devibar.halaman.features.plant.Plant;
import edu.cit.devibar.halaman.features.plant.PlantRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class MaintenanceLogServiceTest {

    // 1. Mock exactly what is @Autowired in your real Service
    @Mock
    private MaintenanceLogRepository maintenanceLogRepository;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MaintenanceLogService maintenanceLogService;

    private UUID mockPlantId;
    private Plant mockPlant;

    @BeforeEach
    void setUp() {
        mockPlantId = UUID.randomUUID();
        mockPlant = new Plant();
        mockPlant.setPlantId(mockPlantId);
    }

    @Test
    void logCareAction_ShouldSaveLogAndPublishEvent() {
        // Arrange
        MaintenanceLogRequest request = new MaintenanceLogRequest();
        request.setPlantId(mockPlantId);
        request.setTaskType("WATERING");
        request.setNotes("Soil was dry; 500ml applied");

        MaintenanceLog mockLog = new MaintenanceLog();
        mockLog.setMaintenanceId(UUID.randomUUID());
        mockLog.setTaskType("WATERING");
        mockLog.setPlant(mockPlant);

        // Tell the mocks how to behave
        when(plantRepository.findById(mockPlantId)).thenReturn(Optional.of(mockPlant));
        when(maintenanceLogRepository.save(any(MaintenanceLog.class))).thenReturn(mockLog);

        // Act
        MaintenanceLog result = maintenanceLogService.logCareAction(request);

        // Assert
        assertNotNull(result);
        assertEquals("WATERING", result.getTaskType());

        // Verify the database saved the log
        verify(maintenanceLogRepository, times(1)).save(any(MaintenanceLog.class));

        // CRUCIAL: Verify that the Event Publisher shouted into the void!
        // This proves your decoupled architecture is working.
        verify(eventPublisher, times(1)).publishEvent(any(MaintenancePerformedEvent.class));
    }

    @Test
    void getPlantMaintenanceLogs_ShouldReturnMappedResponses() {
        // Arrange
        MaintenanceLog log1 = new MaintenanceLog();
        log1.setMaintenanceId(UUID.randomUUID());
        log1.setTaskType("WATERING");
        log1.setNotes("Watered 500ml");
        log1.setCompletedAt(LocalDateTime.now());

        when(maintenanceLogRepository.findByPlantPlantIdAndDeletedAtIsNullOrderByCompletedAtDesc(mockPlantId))
                .thenReturn(Arrays.asList(log1));

        // Act
        List<MaintenanceLogResponse> responses = maintenanceLogService.getPlantMaintenanceLogs(mockPlantId);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("WATERING", responses.get(0).getTaskType());
        assertEquals("Watered 500ml", responses.get(0).getNotes());

        // Verify the repository was queried correctly
        verify(maintenanceLogRepository, times(1))
                .findByPlantPlantIdAndDeletedAtIsNullOrderByCompletedAtDesc(mockPlantId);
    }

    @Test
    void logCareAction_ShouldThrowException_WhenPlantNotFound() {
        MaintenanceLogRequest request = new MaintenanceLogRequest();
        request.setPlantId(mockPlantId);
        
        when(plantRepository.findById(mockPlantId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            maintenanceLogService.logCareAction(request);
        });

        assertEquals("Plant not found", exception.getMessage());
        verify(maintenanceLogRepository, never()).save(any(MaintenanceLog.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void getPlantMaintenanceLogs_ShouldReturnEmptyList_WhenNoLogs() {
        when(maintenanceLogRepository.findByPlantPlantIdAndDeletedAtIsNullOrderByCompletedAtDesc(mockPlantId))
                .thenReturn(Collections.emptyList());

        List<MaintenanceLogResponse> responses = maintenanceLogService.getPlantMaintenanceLogs(mockPlantId);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }
}