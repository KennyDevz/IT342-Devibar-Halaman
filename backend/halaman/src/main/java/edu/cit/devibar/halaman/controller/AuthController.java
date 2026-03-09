package edu.cit.devibar.halaman.controller;

import edu.cit.devibar.halaman.dto.AuthResponse;
import edu.cit.devibar.halaman.dto.LoginRequest;
import edu.cit.devibar.halaman.dto.RegisterRequest;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(response);
    }

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    // GET /api/v1/auth/me
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        AuthResponse.UserDto userDto = new AuthResponse.UserDto();
        userDto.setUserId(user.getUserId().toString());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRole(user.getRole().name());

        AuthResponse.DataPayload dataPayload = new AuthResponse.DataPayload();
        dataPayload.setUser(userDto);

        return ResponseEntity.ok(AuthResponse.success(dataPayload));
    }
}