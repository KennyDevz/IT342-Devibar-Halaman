package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.AuthResponse;
import edu.cit.devibar.halaman.dto.LoginRequest;
import edu.cit.devibar.halaman.dto.RegisterRequest;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.repository.UserRepository;
import edu.cit.devibar.halaman.security.JwtService;
import edu.cit.devibar.halaman.service.strategy.auth.AuthStrategy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import edu.cit.devibar.halaman.dto.GoogleAuthRequest;
import java.util.Collections;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final List<AuthStrategy> strategies;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager, List<AuthStrategy> strategies, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.strategies = strategies;
        this.auditService = auditService;
        ;
    }

    // UNIFIED LOGIN: The Strategy Pattern in action
    public AuthResponse authenticate(String provider, Object request) {
        try {
            AuthStrategy strategy = strategies.stream()
                    .filter(s -> s.supports(provider))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown provider"));

            User user = strategy.authenticate(request);
            return buildTokenResponse(user);

        } catch (BadCredentialsException e) {
            return AuthResponse.error(
                    "AUTH-401",
                    "Invalid Credentials",
                    "The email or password you entered is incorrect."
            );

        } catch (Exception e) {
            return AuthResponse.error(
                    "SYS-500",
                    "Server Error",
                    "Something went wrong on our end. Please try again."
            );
        }
    }


    @Transactional // Protects the database write operation
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.error(
                    "DB-002",
                    "Email already registered",
                    "An account with this email already exists"
            );
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);

        User savedUser = userRepository.save(user);

        auditService.logAction("USER_REGISTER", "New user registered: " + savedUser.getEmail(), savedUser);

        return buildTokenResponse(savedUser);
    }


    // ==========================================
    // PRIVATE HELPER METHODS
    // ==========================================

    private AuthResponse buildTokenResponse(User user) {
        String accessToken  = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse.UserDto userDto = new AuthResponse.UserDto();
        userDto.setUserId(user.getUserId().toString());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRole(user.getRole().name());

        AuthResponse.DataPayload dataPayload = new AuthResponse.DataPayload();
        dataPayload.setUser(userDto);
        dataPayload.setAccessToken(accessToken);
        dataPayload.setRefreshToken(refreshToken);

        return AuthResponse.success(dataPayload);
    }


}