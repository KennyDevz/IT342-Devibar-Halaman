package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.entity.Plant;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class PlantDeletionFacade {

    // The Facade handles the complex orchestration of deleting related entities
    public void executeSoftDelete(Plant plant) {
        LocalDateTime now = LocalDateTime.now();

        // 1. Soft delete the Plant itself
        plant.setDeletedAt(now);

        // 2. Soft delete related Maintenance Logs
        if (plant.getMaintenanceLogs() != null) {
            plant.getMaintenanceLogs().forEach(log -> log.setDeletedAt(now));
        }

        // 3. Soft delete related Care Schedules
        if (plant.getCareSchedules() != null) {
            plant.getCareSchedules().forEach(schedule -> schedule.setDeletedAt(now));
        }

        // 4. Soft delete related Plant Images
        if (plant.getImages() != null) {
            plant.getImages().forEach(image -> image.setDeletedAt(now));
        }
    }
}