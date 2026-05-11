package edu.cit.devibar.halaman.features.plant;

import edu.cit.devibar.halaman.infrastructure.storage.ImageStorageAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantImageServiceTest {

    @Mock
    private PlantImageRepository plantImageRepository;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private ImageStorageAdapter storageAdapter;

    @InjectMocks
    private PlantImageService plantImageService;

    private Plant mockPlant;
    private PlantImage mockImage;
    private UUID mockPlantId;
    private UUID mockImageId;

    @BeforeEach
    void setUp() {
        mockPlantId = UUID.randomUUID();
        mockImageId = UUID.randomUUID();

        mockPlant = new Plant();
        mockPlant.setPlantId(mockPlantId);

        mockImage = new PlantImage();
        mockImage.setImageId(mockImageId);
        mockImage.setPlant(mockPlant);
        mockImage.setFileUrl("http://image.url");
        mockImage.setUploadedAt(LocalDateTime.now());
        mockImage.setCaption("A lovely plant");
    }

    @Test
    void uploadAndSaveImage_ShouldSaveImage_WhenSuccess() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(plantRepository.findById(mockPlantId)).thenReturn(Optional.of(mockPlant));
        when(storageAdapter.saveImage(mockFile)).thenReturn("http://newimage.url");
        when(plantImageRepository.save(any(PlantImage.class))).thenAnswer(i -> i.getArgument(0));

        PlantImage result = plantImageService.uploadAndSaveImage(mockPlantId, mockFile, "New Caption");

        assertNotNull(result);
        assertEquals("http://newimage.url", result.getFileUrl());
        assertEquals("New Caption", result.getCaption());
        assertEquals(mockPlant, result.getPlant());
    }

    @Test
    void uploadAndSaveImage_ShouldThrowException_WhenPlantNotFound() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(plantRepository.findById(mockPlantId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> plantImageService.uploadAndSaveImage(mockPlantId, mockFile, "Caption"));
    }

    @Test
    void getImagesForPlant_ShouldReturnImages() {
        when(plantImageRepository.findByPlantPlantIdAndDeletedAtIsNull(mockPlantId))
                .thenReturn(Collections.singletonList(mockImage));

        List<PlantImage> images = plantImageService.getImagesForPlant(mockPlantId);

        assertEquals(1, images.size());
        assertEquals(mockImage, images.get(0));
    }

    @Test
    void getImageHistory_ShouldReturnImageResponses() {
        when(plantImageRepository.findByPlantPlantIdAndDeletedAtIsNullOrderByUploadedAtAsc(mockPlantId))
                .thenReturn(Collections.singletonList(mockImage));

        List<PlantImageResponse> responses = plantImageService.getImageHistory(mockPlantId);

        assertEquals(1, responses.size());
        assertEquals("http://image.url", responses.get(0).getFileUrl());
        assertEquals("A lovely plant", responses.get(0).getCaption());
    }

    @Test
    void deleteImage_ShouldDeleteImage() {
        when(plantImageRepository.findById(mockImageId)).thenReturn(Optional.of(mockImage));

        plantImageService.deleteImage(mockPlantId, mockImageId);

        verify(storageAdapter, times(1)).deleteImage("http://image.url");
        verify(plantImageRepository, times(1)).delete(mockImage);
    }

    @Test
    void deleteImage_ShouldThrowException_WhenImageNotFound() {
        when(plantImageRepository.findById(mockImageId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> plantImageService.deleteImage(mockPlantId, mockImageId));
    }

    @Test
    void deleteImage_ShouldThrowException_WhenImageBelongsToDifferentPlant() {
        Plant differentPlant = new Plant();
        differentPlant.setPlantId(UUID.randomUUID());
        mockImage.setPlant(differentPlant);

        when(plantImageRepository.findById(mockImageId)).thenReturn(Optional.of(mockImage));

        assertThrows(RuntimeException.class, () -> plantImageService.deleteImage(mockPlantId, mockImageId));
    }
}
