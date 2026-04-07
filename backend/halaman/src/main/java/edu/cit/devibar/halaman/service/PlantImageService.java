package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.entity.PlantImage;
import edu.cit.devibar.halaman.repository.PlantImageRepository;
import edu.cit.devibar.halaman.repository.PlantRepository;
import edu.cit.devibar.halaman.service.storage.ImageStorageAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Service
public class PlantImageService {

    @Autowired
    private PlantImageRepository plantImageRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private ImageStorageAdapter storageAdapter;

    public PlantImage uploadAndSaveImage(UUID plantId, MultipartFile file) throws Exception {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        // 2. REFACTORED: Delegate the physical saving to the adapter
        String fileUrl = storageAdapter.saveImage(file);

        PlantImage image = new PlantImage();
        image.setPlant(plant);
        image.setFileUrl(fileUrl);

        return plantImageRepository.save(image);
    }

    public List<PlantImage> getImagesForPlant(UUID plantId) {
        return plantImageRepository.findByPlantPlantIdAndDeletedAtIsNull(plantId);
    }
}