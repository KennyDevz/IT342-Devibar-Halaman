package edu.cit.devibar.halaman.features.user;

import edu.cit.devibar.halaman.features.auth.User;
import edu.cit.devibar.halaman.features.auth.UserRepository;
import edu.cit.devibar.halaman.features.admin.AuditService;
import edu.cit.devibar.halaman.features.admin.SystemLogRepository;
import edu.cit.devibar.halaman.features.plant.Plant;
import edu.cit.devibar.halaman.features.plant.PlantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final SystemLogRepository systemLogRepository;
    private final PlantRepository plantRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuditService auditService,
                       SystemLogRepository systemLogRepository,
                       PlantRepository plantRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.systemLogRepository = systemLogRepository;
        this.plantRepository = plantRepository;
    }

    // Update first name and last name of the authenticated user.
    @Transactional
    public Map<String, String> updateProfile(User currentUser, UpdateProfileRequest request) {
        currentUser.setFirstName(request.getFirstName());
        currentUser.setLastName(request.getLastName());
        userRepository.save(currentUser);

        auditService.logAction("USER_UPDATE_PROFILE",
                "User updated profile: " + currentUser.getEmail(), currentUser);

        return Map.of(
                "firstName", currentUser.getFirstName(),
                "lastName", currentUser.getLastName(),
                "email", currentUser.getEmail()
        );
    }

    // Change the password — validates the current password first.
    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "New password must be different from the current password");
        }

        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        auditService.logAction("USER_CHANGE_PASSWORD",
                "User changed password: " + currentUser.getEmail(), currentUser);
    }

    @Transactional
    public void deleteAccount(User currentUser) {
        systemLogRepository.detachActor(currentUser.getUserId());
        List<Plant> plants = plantRepository.findByUserUserId(currentUser.getUserId());
        plantRepository.deleteAll(plants);
        userRepository.delete(currentUser);
    }
}
