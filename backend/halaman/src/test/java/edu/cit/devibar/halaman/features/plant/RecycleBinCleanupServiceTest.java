package edu.cit.devibar.halaman.features.plant;

import edu.cit.devibar.halaman.core.EmailService;
import edu.cit.devibar.halaman.features.auth.User;
import edu.cit.devibar.halaman.infrastructure.storage.ImageStorageAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecycleBinCleanupServiceTest {

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private ImageStorageAdapter imageStorageAdapter;

    @InjectMocks
    private RecycleBinCleanupService recycleBinCleanupService;

    private Plant mockPlant;

    @BeforeEach
    void setUp() {
        mockPlant = new Plant();
        mockPlant.setNickname("Old Plant");
        
        User mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockUser.setFirstName("John");
        mockPlant.setUser(mockUser);
    }

    @Test
    void emptyOldRecycleBinItems_ShouldDeleteOldPlantsAndImages() {
        PlantImage image = new PlantImage();
        image.setFileUrl("http://image.url");
        mockPlant.setImages(Collections.singletonList(image));

        when(plantRepository.findOldPlantsWithImages(any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockPlant));

        recycleBinCleanupService.emptyOldRecycleBinItems();

        verify(imageStorageAdapter, times(1)).deleteImage("http://image.url");
        verify(plantRepository, times(1)).deleteAll(Collections.singletonList(mockPlant));
    }

    @Test
    void emptyOldRecycleBinItems_ShouldDoNothing_WhenNoOldPlants() {
        when(plantRepository.findOldPlantsWithImages(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        recycleBinCleanupService.emptyOldRecycleBinItems();

        verify(imageStorageAdapter, never()).deleteImage(anyString());
        verify(plantRepository, never()).deleteAll(any());
    }

    @Test
    void sendDeletionWarnings_ShouldSendEmails() {
        when(plantRepository.findPlantsApproachingDeletion(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockPlant));

        recycleBinCleanupService.sendDeletionWarnings();

        verify(emailService, times(1)).sendDeletionWarningEmail(
                "user@example.com", "John", "Old Plant", 5);
    }

    @Test
    void sendDeletionWarnings_ShouldDoNothing_WhenNoPlants() {
        when(plantRepository.findPlantsApproachingDeletion(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        recycleBinCleanupService.sendDeletionWarnings();

        verify(emailService, never()).sendDeletionWarningEmail(anyString(), anyString(), anyString(), anyInt());
    }
}
