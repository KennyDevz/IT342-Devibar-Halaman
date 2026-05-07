package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.entity.SystemLog;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.repository.SystemLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final SystemLogRepository logRepository;

    public AuditService(SystemLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void logAction(String actionType, String description, User actor) {
        SystemLog log = new SystemLog();
        log.setActionType(actionType);
        log.setDescription(description);
        log.setActor(actor);
        logRepository.save(log);
    }
}