package edu.cit.devibar.halaman.features.user;

import edu.cit.devibar.halaman.core.ApiResponseFactory;
import edu.cit.devibar.halaman.features.auth.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * PUT /api/users/profile
     * Update the current user's first and last name.
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {

        Map<String, String> updated = userService.updateProfile(currentUser, request);
        return ApiResponseFactory.success("Profile updated successfully", updated);
    }

    /**
     * PUT /api/users/password
     * Change the current user's password.
     */
    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(currentUser, request);
        return ApiResponseFactory.success("Password changed successfully", null);
    }

    /**
     * DELETE /api/users/me
     * Permanently delete the current user's account.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, Object>> deleteAccount(
            @AuthenticationPrincipal User currentUser) {

        userService.deleteAccount(currentUser);
        return ApiResponseFactory.success("Account deleted successfully", null);
    }
}
