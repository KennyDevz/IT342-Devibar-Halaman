package edu.cit.devibar.halaman.features.user;

import edu.cit.devibar.halaman.features.auth.User;
import edu.cit.devibar.halaman.features.auth.UserRepository;
import edu.cit.devibar.halaman.features.admin.AuditService;
import edu.cit.devibar.halaman.features.admin.SystemLogRepository;
import edu.cit.devibar.halaman.features.plant.Plant;
import edu.cit.devibar.halaman.features.plant.PlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService auditService;

    @Mock
    private SystemLogRepository systemLogRepository;

    @Mock
    private PlantRepository plantRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPasswordHash("hashedPassword");
    }

    @Test
    void updateProfile_Success() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");

        Map<String, String> result = userService.updateProfile(testUser, request);

        assertEquals("Jane", result.get("firstName"));
        assertEquals("Smith", result.get("lastName"));
        assertEquals("test@example.com", result.get("email"));
        verify(userRepository).save(testUser);
        verify(auditService).logAction(eq("USER_UPDATE_PROFILE"), anyString(), eq(testUser));
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");

        when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");

        assertDoesNotThrow(() -> userService.changePassword(testUser, request));

        assertEquals("newHashedPassword", testUser.getPasswordHash());
        verify(userRepository).save(testUser);
        verify(auditService).logAction(eq("USER_CHANGE_PASSWORD"), anyString(), eq(testUser));
    }

    @Test
    void changePassword_IncorrectCurrentPassword_ThrowsException() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword");

        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> userService.changePassword(testUser, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_NewPasswordSameAsCurrent_ThrowsException() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("oldPassword");

        when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.changePassword(testUser, request));
    }

    @Test
    void deleteAccount_Success() {
        List<Plant> plants = new ArrayList<>();
        plants.add(new Plant());
        
        when(plantRepository.findByUserUserId(testUser.getUserId())).thenReturn(plants);

        assertDoesNotThrow(() -> userService.deleteAccount(testUser));

        verify(systemLogRepository).detachActor(testUser.getUserId());
        verify(plantRepository).deleteAll(plants);
        verify(userRepository).delete(testUser);
    }
}
