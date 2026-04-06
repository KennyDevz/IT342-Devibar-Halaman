package edu.cit.devibar.halaman.controller;

import edu.cit.devibar.halaman.dto.MaintenanceLogRequest;
import edu.cit.devibar.halaman.dto.MaintenanceLogResponse;
import edu.cit.devibar.halaman.entity.MaintenanceLog;
import edu.cit.devibar.halaman.service.MaintenanceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceLogController {

    @Autowired
    private MaintenanceLogService maintenanceLogService;

    // POST: /api/maintenance
    @PostMapping
    public ResponseEntity<?> logMaintenanceAction(@RequestBody MaintenanceLogRequest request) {
        try {
            MaintenanceLog savedLog = maintenanceLogService.logCareAction(request);
            Map<String, Object> data = new HashMap<>();
            data.put("maintenanceId", savedLog.getMaintenanceId());
            data.put("taskType", savedLog.getTaskType());
            data.put("completedAt", savedLog.getCompletedAt().toString());
            return ResponseEntity.ok(Map.of("success", true, "data", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", Map.of("message", "Failed to log maintenance")));
        }
    }

    // GET: /api/maintenance/{plantId}
    @GetMapping("/{plantId}")
    public ResponseEntity<?> getPlantMaintenanceLogs(@PathVariable UUID plantId) {
        try {
            List<MaintenanceLogResponse> logs = maintenanceLogService.getPlantMaintenanceLogs(plantId);
            return ResponseEntity.ok(Map.of("success", true, "data", logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", Map.of("message", "Failed to fetch logs")));
        }
    }
}