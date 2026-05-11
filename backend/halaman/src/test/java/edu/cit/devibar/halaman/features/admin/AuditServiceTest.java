package edu.cit.devibar.halaman.features.admin;

import edu.cit.devibar.halaman.features.auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @Mock
    private SystemLogRepository logRepository;

    @InjectMocks
    private AuditService auditService;

    private User mockActor;

    @BeforeEach
    void setUp() {
        mockActor = new User();
        mockActor.setUserId(UUID.randomUUID());
        mockActor.setEmail("admin@example.com");
    }

    @Test
    void logAction_ShouldSaveSystemLog() {
        String actionType = "USER_BAN";
        String description = "Banned a spam user";

        auditService.logAction(actionType, description, mockActor);

        ArgumentCaptor<SystemLog> logCaptor = ArgumentCaptor.forClass(SystemLog.class);
        verify(logRepository, times(1)).save(logCaptor.capture());

        SystemLog savedLog = logCaptor.getValue();
        assertEquals(actionType, savedLog.getActionType());
        assertEquals(description, savedLog.getDescription());
        assertEquals(mockActor, savedLog.getActor());
    }
}
