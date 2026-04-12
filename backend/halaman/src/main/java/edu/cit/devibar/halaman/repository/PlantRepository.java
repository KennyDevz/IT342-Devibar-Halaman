package edu.cit.devibar.halaman.repository;

import edu.cit.devibar.halaman.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlantRepository extends JpaRepository<Plant, UUID> {

    // Get all active plants for a user (not soft deleted)
    List<Plant> findByUserUserIdAndDeletedAtIsNull(UUID userId);

    // Finds plants that BELONG to the user and HAVE a deleted timestamp
    List<Plant> findByUserUserIdAndDeletedAtIsNotNull(UUID userId);

    // Get single active plant by id
    Optional<Plant> findByPlantIdAndDeletedAtIsNull(UUID plantId);

    // Check if nickname already exists for a user
    boolean existsByUserUserIdAndNicknameAndDeletedAtIsNull(
            UUID userId, String nickname
    );
}