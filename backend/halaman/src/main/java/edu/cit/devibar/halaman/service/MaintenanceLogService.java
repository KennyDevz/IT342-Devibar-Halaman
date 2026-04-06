package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.MaintenanceLogRequest;
import edu.cit.devibar.halaman.dto.MaintenanceLogResponse;
import edu.cit.devibar.halaman.entity.CareSchedule;
import edu.cit.devibar.halaman.entity.MaintenanceLog;
import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.repository.CareScheduleRepository;
import edu.cit.devibar.halaman.repository.MaintenanceLogRepository;
import edu.cit.devibar.halaman.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MaintenanceLogService {

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private CareScheduleRepository careScheduleRepository;

    @Transactional
    public MaintenanceLog logCareAction(MaintenanceLogRequest request) {
        Plant plant = plantRepository.findById(request.getPlantId())
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        MaintenanceLog log = new MaintenanceLog();
        log.setPlant(plant);
        log.setTaskType(request.getTaskType());
        log.setNotes(request.getNotes());
        maintenanceLogRepository.save(log);

        Optional<CareSchedule> scheduleOpt = careScheduleRepository
                .findFirstByPlantPlantIdAndTaskTypeAndDeletedAtIsNull(plant.getPlantId(), request.getTaskType());

        if (scheduleOpt.isPresent()) {
            CareSchedule schedule = scheduleOpt.get();
            schedule.setNextDueDate(LocalDate.now().plusDays(schedule.getFrequencyDays()));
            careScheduleRepository.save(schedule);
        } else {
            CareSchedule newSchedule = new CareSchedule();
            newSchedule.setPlant(plant);
            newSchedule.setTaskType(request.getTaskType());
            Integer frequency = plant.getWateringFrequencyDays() != null ? plant.getWateringFrequencyDays() : 7;
            newSchedule.setFrequencyDays(frequency);
            newSchedule.setNextDueDate(LocalDate.now().plusDays(frequency));
            careScheduleRepository.save(newSchedule);
        }
        return log;
    }

    public List<MaintenanceLogResponse> getPlantMaintenanceLogs(UUID plantId) {

        // 1. Fetch raw entities from the database
        java.util.List<MaintenanceLog> rawLogs =
                maintenanceLogRepository.findByPlantPlantIdAndDeletedAtIsNullOrderByCompletedAtDesc(plantId);

        // 2. Convert each raw entity into a clean DTO
        return rawLogs.stream().map(log -> new MaintenanceLogResponse(
                log.getMaintenanceId(),
                log.getTaskType(),
                log.getNotes(),
                log.getCompletedAt()
        )).collect(Collectors.toList());
    }
}