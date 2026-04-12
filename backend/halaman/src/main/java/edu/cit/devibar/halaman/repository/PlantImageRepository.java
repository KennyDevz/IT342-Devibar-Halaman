package edu.cit.devibar.halaman.repository;

import edu.cit.devibar.halaman.entity.PlantImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlantImageRepository extends JpaRepository<PlantImage, UUID> {
    List<PlantImage> findByPlantPlantIdAndDeletedAtIsNull(UUID plantId);

    List<PlantImage> findByPlantPlantIdAndDeletedAtIsNullOrderByUploadedAtAsc(UUID plantId);
}