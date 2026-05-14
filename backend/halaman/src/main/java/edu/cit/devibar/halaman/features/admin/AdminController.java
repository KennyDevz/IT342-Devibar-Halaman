package edu.cit.devibar.halaman.features.admin;

import edu.cit.devibar.halaman.core.ApiResponseFactory;
import edu.cit.devibar.halaman.features.auth.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // GET /api/admin/metrics
    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics() {
        return ApiResponseFactory.success("Metrics fetched", adminService.getSystemMetrics());
    }

    // GET /api/admin/users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ApiResponseFactory.success("Users fetched", adminService.getAllUsers());
    }

    // PUT /api/admin/users/{id}/toggle-status
    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable UUID id) {
        User updatedUser = adminService.toggleUserStatus(id);
        return ApiResponseFactory.success("User status updated", updatedUser);
    }

    @GetMapping("/activity")
    public ResponseEntity<?> getRecentActivity() {
        return ApiResponseFactory.success("Activity fetched", adminService.getRecentActivity());
    }

    @GetMapping("/images")
    public ResponseEntity<?> getAllImages() {
        return ApiResponseFactory.success("Images fetched", adminService.getAllModerationImages());
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID id) {
        adminService.deleteImage(id);
        return ApiResponseFactory.success("Image successfully deleted", null);
    }
}