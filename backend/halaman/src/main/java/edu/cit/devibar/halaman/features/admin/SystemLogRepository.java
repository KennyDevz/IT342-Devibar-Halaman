package edu.cit.devibar.halaman.features.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, UUID> {
    List<SystemLog> findTop5ByOrderByCreatedAtDesc();

    // Detach all system_log rows from a user so the user row can be deleted
    @Modifying
    @Query("UPDATE SystemLog l SET l.actor = NULL WHERE l.actor.userId = :userId")
    void detachActor(@Param("userId") UUID userId);
}