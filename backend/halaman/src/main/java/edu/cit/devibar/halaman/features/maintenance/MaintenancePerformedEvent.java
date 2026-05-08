package edu.cit.devibar.halaman.features.maintenance;

import org.springframework.context.ApplicationEvent;

public class MaintenancePerformedEvent extends ApplicationEvent {

    private final MaintenanceLog log;

    public MaintenancePerformedEvent(Object source, MaintenanceLog log) {
        super(source);
        this.log = log;
    }

    public MaintenanceLog getLog() {
        return log;
    }
}