package edu.cit.devibar.halaman.repository;

import edu.cit.devibar.halaman.entity.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, UUID> {
    // Fetches the log history for a specific plant, newest first
    List<MaintenanceLog> findByPlantPlantIdAndDeletedAtIsNullOrderByCompletedAtDesc(UUID plantId);
}