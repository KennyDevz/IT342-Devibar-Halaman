package edu.cit.devibar.halaman.repository;

import edu.cit.devibar.halaman.entity.CareSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CareScheduleRepository extends JpaRepository<CareSchedule, UUID> {

    // Finds the active watering schedule for a specific plant
    Optional<CareSchedule> findFirstByPlantPlantIdAndTaskTypeAndDeletedAtIsNull(UUID plantId, String taskType);

}