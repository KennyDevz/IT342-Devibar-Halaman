package edu.cit.devibar.halaman.repository;

import edu.cit.devibar.halaman.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    List<Plant> findByDeletedAtLessThanEqual(LocalDateTime thresholdDate);

    @Query("SELECT DISTINCT p FROM Plant p LEFT JOIN FETCH p.images WHERE p.deletedAt <= :threshold")
    List<Plant> findOldPlantsWithImages(@Param("threshold") LocalDateTime threshold);

    // Finds plants deleted between 'start' (26 days ago) and 'end' (25 days ago)
    @Query("SELECT p FROM Plant p WHERE p.deletedAt >= :start AND p.deletedAt < :end")
    List<Plant> findPlantsApproachingDeletion(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}