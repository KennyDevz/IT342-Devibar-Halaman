package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.entity.PlantImage;
import edu.cit.devibar.halaman.repository.PlantImageRepository;
import edu.cit.devibar.halaman.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class PlantImageService {

    @Autowired
    private PlantImageRepository plantImageRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public PlantImage uploadAndSaveImage(UUID plantId, MultipartFile file) throws IOException {
        // 1. Verify the plant exists
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        // 2. Save the physical file and get the URL
        String fileUrl = fileStorageService.storeFile(file);

        // 3. Save to the database
        PlantImage image = new PlantImage();
        image.setPlant(plant);
        image.setFileUrl(fileUrl);

        return plantImageRepository.save(image);
    }

    // Add this new method to your existing PlantImageService
    public java.util.List<PlantImage> getImagesForPlant(UUID plantId) {
        return plantImageRepository.findByPlantPlantIdAndDeletedAtIsNull(plantId);
    }
}