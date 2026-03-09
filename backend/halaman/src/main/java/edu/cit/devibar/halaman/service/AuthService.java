package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.AuthResponse;
import edu.cit.devibar.halaman.dto.LoginRequest;
import edu.cit.devibar.halaman.dto.RegisterRequest;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.repository.UserRepository;
import edu.cit.devibar.halaman.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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

        userRepository.save(user);

        // Call the new helper method!
        return buildTokenResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return AuthResponse.error(
                    "AUTH-001",
                    "Invalid credentials",
                    "Email or password is incorrect"
            );
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        // Call the new helper method!
        return buildTokenResponse(user);
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