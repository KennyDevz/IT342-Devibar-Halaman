package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.entity.PlantImage;
import edu.cit.devibar.halaman.repository.PlantRepository;
import edu.cit.devibar.halaman.service.storage.ImageStorageAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecycleBinCleanupService {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ImageStorageAdapter imageStorageAdapter;

    /**
     * This method runs automatically every day at 12:00 AM (Midnight)
     * Cron format: Seconds Minutes Hours DayOfMonth Month DayOfWeek
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void emptyOldRecycleBinItems() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);

        // 🌟 1. Use the new JOIN FETCH method
        List<Plant> oldPlants = plantRepository.findOldPlantsWithImages(threshold);

        if (!oldPlants.isEmpty()) {

            // 2. Loop through and use your existing deleteImage method
            for (Plant plant : oldPlants) {
                for (PlantImage image : plant.getImages()) {
                    imageStorageAdapter.deleteImage(image.getFileUrl());
                }
            }
            plantRepository.deleteAll(oldPlants);

        }
    }


    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(readOnly = true)
    public void sendDeletionWarnings() {

        // The window: Between 26 days ago and 25 days ago
        LocalDateTime endOfWindow = LocalDateTime.now().minusDays(25);
        LocalDateTime startOfWindow = LocalDateTime.now().minusDays(26);

        // Fetch the plants in the danger zone
        List<Plant> plantsToWarn = plantRepository.findPlantsApproachingDeletion(startOfWindow, endOfWindow);

        // Loop through them and fire off the emails
        for (Plant plant : plantsToWarn) {
            emailService.sendDeletionWarningEmail(
                    plant.getUser().getEmail(),
                    plant.getUser().getFirstName(),
                    plant.getNickname(),
                    5 // Days remaining
            );
        }
    }
}