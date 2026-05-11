package edu.cit.devibar.halaman.features.admin;

import edu.cit.devibar.halaman.features.auth.User;
import edu.cit.devibar.halaman.features.auth.UserRepository;
import edu.cit.devibar.halaman.features.plant.Plant;
import edu.cit.devibar.halaman.features.plant.PlantImage;
import edu.cit.devibar.halaman.features.plant.PlantImageRepository;
import edu.cit.devibar.halaman.features.plant.PlantRepository;
import edu.cit.devibar.halaman.infrastructure.storage.ImageStorageAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private PlantImageRepository imageRepository;

    @Mock
    private SystemLogRepository systemLogRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private ImageStorageAdapter storageAdapter;

    @InjectMocks
    private AdminService adminService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserId(UUID.randomUUID());
        mockUser.setStatus("ACTIVE");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
    }

    @Test
    void getSystemMetrics_ShouldReturnMetrics() {
        when(userRepository.count()).thenReturn(10L);
        when(plantRepository.count()).thenReturn(50L);
        when(imageRepository.count()).thenReturn(100L);
        when(userRepository.countByStatus("ACTIVE")).thenReturn(8L);

        Map<String, Object> metrics = adminService.getSystemMetrics();

        assertEquals(10L, metrics.get("totalUsers"));
        assertEquals(50L, metrics.get("totalPlants"));
        assertEquals(100L, metrics.get("totalImages"));
        assertEquals(8L, metrics.get("activeUsers"));
    }

    @Test
    void toggleUserStatus_ShouldToggleToSuspended_WhenActive() {
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = adminService.toggleUserStatus(mockUser.getUserId());

        assertEquals("SUSPENDED", updatedUser.getStatus());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void toggleUserStatus_ShouldToggleToActive_WhenSuspended() {
        mockUser.setStatus("SUSPENDED");
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = adminService.toggleUserStatus(mockUser.getUserId());

        assertEquals("ACTIVE", updatedUser.getStatus());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void toggleUserStatus_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.toggleUserStatus(mockUser.getUserId()));
    }

    @Test
    void getRecentActivity_ShouldReturnActivityLogs() {
        SystemLog log = new SystemLog();
        log.setLogId(UUID.randomUUID());
        log.setActionType("LOGIN");
        log.setDescription("User logged in");
        log.setCreatedAt(LocalDateTime.now());

        when(systemLogRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(Collections.singletonList(log));

        List<ActivityLogDto> logs = adminService.getRecentActivity();

        assertEquals(1, logs.size());
        assertEquals("LOGIN", logs.get(0).getType());
        assertEquals("User logged in", logs.get(0).getText());
    }

    @Test
    void getAllModerationImages_ShouldReturnImageDtos() {
        Plant plant = new Plant();
        plant.setNickname("Fern");
        plant.setUser(mockUser);

        PlantImage image = new PlantImage();
        image.setImageId(UUID.randomUUID());
        image.setFileUrl("http://image.url");
        image.setUploadedAt(LocalDateTime.now());
        image.setPlant(plant);

        when(imageRepository.findAll()).thenReturn(Collections.singletonList(image));

        List<ImageModerationDto> dtos = adminService.getAllModerationImages();

        assertEquals(1, dtos.size());
        assertEquals("http://image.url", dtos.get(0).getImageUrl());
        assertEquals("John Doe", dtos.get(0).getUploaderName());
        assertEquals("Fern", dtos.get(0).getPlantName());
    }

    @Test
    void deleteImage_ShouldDeleteImageAndLogAction() {
        Plant plant = new Plant();
        plant.setNickname("Fern");

        PlantImage image = new PlantImage();
        image.setImageId(UUID.randomUUID());
        image.setFileUrl("http://image.url");
        image.setPlant(plant);

        when(imageRepository.findById(image.getImageId())).thenReturn(Optional.of(image));

        adminService.deleteImage(image.getImageId());

        verify(storageAdapter, times(1)).deleteImage("http://image.url");
        verify(imageRepository, times(1)).delete(image);
        verify(auditService, times(1)).logAction(eq("IMAGE_DELETE"), anyString(), isNull());
    }

    @Test
    void deleteImage_ShouldThrowException_WhenImageNotFound() {
        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.deleteImage(UUID.randomUUID()));
    }
}
