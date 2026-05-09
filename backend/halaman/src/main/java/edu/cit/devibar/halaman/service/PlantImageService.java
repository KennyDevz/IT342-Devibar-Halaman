package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.PlantImageResponse;
import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.entity.PlantImage;
import edu.cit.devibar.halaman.repository.PlantImageRepository;
import edu.cit.devibar.halaman.repository.PlantRepository;
import edu.cit.devibar.halaman.service.storage.ImageStorageAdapter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlantImageService {

    @Autowired
    private PlantImageRepository plantImageRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private ImageStorageAdapter storageAdapter;

    public PlantImage uploadAndSaveImage(UUID plantId, MultipartFile file, String caption) throws Exception {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        // 2. REFACTORED: Delegate the physical saving to the adapter
        String fileUrl = storageAdapter.saveImage(file);

        PlantImage image = new PlantImage();
        image.setPlant(plant);
        image.setFileUrl(fileUrl);
        image.setCaption(caption);

        return plantImageRepository.save(image);
    }

    public List<PlantImage> getImagesForPlant(UUID plantId) {
        return plantImageRepository.findByPlantPlantIdAndDeletedAtIsNull(plantId);
    }

    public List<PlantImageResponse> getImageHistory(UUID plantId) {
        return plantImageRepository.findByPlantPlantIdAndDeletedAtIsNullOrderByUploadedAtAsc(plantId)
                .stream()
                .map(image -> new PlantImageResponse(image.getImageId(),image.getFileUrl(), image.getUploadedAt(), image.getCaption()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(UUID plantId, UUID imageId) {
        PlantImage image = plantImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (!image.getPlant().getPlantId().equals(plantId)) {
            throw new RuntimeException("Image does not belong to this plant");
        }

        storageAdapter.deleteImage(image.getFileUrl());

        plantImageRepository.delete(image);
    }
}