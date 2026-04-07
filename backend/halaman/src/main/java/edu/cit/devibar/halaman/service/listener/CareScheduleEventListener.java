package edu.cit.devibar.halaman.service.listener;

import edu.cit.devibar.halaman.entity.CareSchedule;
import edu.cit.devibar.halaman.entity.MaintenanceLog;
import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.event.MaintenancePerformedEvent;
import edu.cit.devibar.halaman.repository.CareScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class CareScheduleEventListener {

    @Autowired
    private CareScheduleRepository careScheduleRepository;

    @EventListener
    public void handleMaintenanceEvent(MaintenancePerformedEvent event) {
        MaintenanceLog log = event.getLog();
        Plant plant = log.getPlant();
        String taskType = log.getTaskType();

        Optional<CareSchedule> scheduleOpt = careScheduleRepository
                .findFirstByPlantPlantIdAndTaskTypeAndDeletedAtIsNull(plant.getPlantId(), taskType);

        if (scheduleOpt.isPresent()) {
            CareSchedule schedule = scheduleOpt.get();
            schedule.setNextDueDate(LocalDate.now().plusDays(schedule.getFrequencyDays()));
            careScheduleRepository.save(schedule);
        } else {
            CareSchedule newSchedule = new CareSchedule();
            newSchedule.setPlant(plant);
            newSchedule.setTaskType(taskType);
            Integer frequency = plant.getWateringFrequencyDays() != null ? plant.getWateringFrequencyDays() : 7;
            newSchedule.setFrequencyDays(frequency);
            newSchedule.setNextDueDate(LocalDate.now().plusDays(frequency));
            careScheduleRepository.save(newSchedule);
        }
    }
}
