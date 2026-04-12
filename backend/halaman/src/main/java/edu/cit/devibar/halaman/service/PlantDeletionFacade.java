package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.service.storage.ImageStorageAdapter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PlantDeletionFacade {

    private final ImageStorageAdapter storageAdapter;

    public PlantDeletionFacade(ImageStorageAdapter storageAdapter) {
        this.storageAdapter = storageAdapter;
    }

    // 1. SOFT DELETE: Move to Recycle Bin
    public void executeSoftDelete(Plant plant) {
        LocalDateTime now = LocalDateTime.now();

        plant.setDeletedAt(now);

        if (plant.getMaintenanceLogs() != null) {
            plant.getMaintenanceLogs().forEach(log -> log.setDeletedAt(now));
        }

        if (plant.getCareSchedules() != null) {
            plant.getCareSchedules().forEach(schedule -> schedule.setDeletedAt(now));
        }

        if (plant.getImages() != null) {
            plant.getImages().forEach(image -> image.setDeletedAt(now));
        }
    }

    // 2. RESTORE: Bring back from Recycle Bin
    public void executeRestore(Plant plant) {
        // Erase the timestamp to make everything active again
        plant.setDeletedAt(null);

        if (plant.getMaintenanceLogs() != null) {
            plant.getMaintenanceLogs().forEach(log -> log.setDeletedAt(null));
        }

        if (plant.getCareSchedules() != null) {
            plant.getCareSchedules().forEach(schedule -> schedule.setDeletedAt(null));
        }

        if (plant.getImages() != null) {
            plant.getImages().forEach(image -> image.setDeletedAt(null));
        }
    }

    // 3. PERMANENT DELETE: Clean up physical files
    public void executePermanentDelete(Plant plant) {
        // We must delete the actual image files from Cloudinary so we don't waste cloud storage!
        if (plant.getImages() != null) {
            plant.getImages().forEach(image -> {
                if (image.getFileUrl() != null) {
                    // Extract the public ID from the URL and delete it from Cloudinary
                    storageAdapter.deleteImage(image.getFileUrl());
                }
            });
        }
    }
}