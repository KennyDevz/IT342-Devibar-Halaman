package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.MaintenanceLogRequest;
import edu.cit.devibar.halaman.dto.MaintenanceLogResponse;
import edu.cit.devibar.halaman.entity.MaintenanceLog;
import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.event.MaintenancePerformedEvent;
import edu.cit.devibar.halaman.repository.MaintenanceLogRepository;
import edu.cit.devibar.halaman.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MaintenanceLogService {

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @Transactional
    public MaintenanceLog logCareAction(MaintenanceLogRequest request) {
        Plant plant = plantRepository.findById(request.getPlantId())
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        MaintenanceLog log = new MaintenanceLog();
        log.setPlant(plant);
        log.setTaskType(request.getTaskType());
        log.setNotes(request.getNotes());

        MaintenanceLog savedLog = maintenanceLogRepository.save(log);

        // 2. REFACTORED: Broadcast the event into the void!
        eventPublisher.publishEvent(new MaintenancePerformedEvent(this, savedLog));

        return savedLog;
    }

    public List<MaintenanceLogResponse> getPlantMaintenanceLogs(UUID plantId) {
        List<MaintenanceLog> rawLogs =
                maintenanceLogRepository.findByPlantPlantIdAndDeletedAtIsNullOrderByCompletedAtDesc(plantId);

        return rawLogs.stream().map(log -> new MaintenanceLogResponse(
                log.getMaintenanceId(),
                log.getTaskType(),
                log.getNotes(),
                log.getCompletedAt()
        )).collect(Collectors.toList());
    }
}