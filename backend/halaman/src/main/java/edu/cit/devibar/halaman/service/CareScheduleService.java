package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.entity.CareSchedule;
import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.repository.CareScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class CareScheduleService {

    @Autowired
    private CareScheduleRepository careScheduleRepository;

    // Generates the initial schedule when a new plant is added
    public CareSchedule createInitialSchedule(Plant plant, Integer frequencyDays) {
        CareSchedule schedule = new CareSchedule();
        schedule.setPlant(plant);
        schedule.setTaskType("WATERING");
        schedule.setFrequencyDays(frequencyDays);
        schedule.setNextDueDate(LocalDate.now().plusDays(frequencyDays));

        return careScheduleRepository.save(schedule);
    }

    // Fetches the schedule for the frontend to calculate the "Due Today/Overdue" status
    public Optional<CareSchedule> getWateringScheduleForPlant(UUID plantId) {
        return careScheduleRepository.findFirstByPlantPlantIdAndTaskTypeAndDeletedAtIsNull(plantId, "WATERING");
    }
}