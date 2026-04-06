package edu.cit.devibar.halaman.controller;

import edu.cit.devibar.halaman.entity.CareSchedule;
import edu.cit.devibar.halaman.service.CareScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/plants")
public class CareScheduleController {

    @Autowired
    private CareScheduleService careScheduleService;

    @GetMapping("/{id}/schedule")
    public ResponseEntity<?> getPlantSchedule(@PathVariable UUID id) {
        try {
            Optional<CareSchedule> scheduleOpt = careScheduleService.getWateringScheduleForPlant(id);

            if (scheduleOpt.isPresent()) {
                CareSchedule schedule = scheduleOpt.get();
                Map<String, Object> data = new HashMap<>();
                data.put("careId", schedule.getCareId());
                data.put("taskType", schedule.getTaskType());
                data.put("nextDueDate", schedule.getNextDueDate().toString());
                data.put("frequencyDays", schedule.getFrequencyDays());

                return ResponseEntity.ok(Map.of("success", true, "data", data));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"success\":false, \"error\":{\"message\":\"Schedule not found\"}}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\":false, \"error\":{\"message\":\"Failed to fetch schedule\"}}");
        }
    }
}