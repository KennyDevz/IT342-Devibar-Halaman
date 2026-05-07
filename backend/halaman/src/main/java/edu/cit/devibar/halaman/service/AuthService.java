package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.AuthResponse;
import edu.cit.devibar.halaman.dto.RegisterRequest;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.repository.UserRepository;
import edu.cit.devibar.halaman.security.JwtService;
import edu.cit.devibar.halaman.service.strategy.auth.AuthStrategy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final List<AuthStrategy> strategies;
    private final AuditService auditService;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager, List<AuthStrategy> strategies, AuditService auditService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.strategies = strategies;
        this.auditService = auditService;
        this.emailService = emailService;
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

            if (user != null && !user.getIsVerified()) {
                return AuthResponse.error(
                        "AUTH-403",
                        "Account Not Verified",
                        "Please verify your email before logging in. Check your inbox for the OTP code."
                );
            }

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

        String generatedOtp = String.format("%06d", new Random().nextInt(999999));
        user.setOtpCode(generatedOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        user.setIsVerified(false);

        User savedUser = userRepository.save(user);

        auditService.logAction("USER_REGISTER", "New user registered: " + savedUser.getEmail(), savedUser);

        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getFirstName(), generatedOtp);

        AuthResponse.DataPayload payload = new AuthResponse.DataPayload();

        AuthResponse.UserDto userDto = new AuthResponse.UserDto();
        userDto.setUserId(savedUser.getUserId().toString());
        userDto.setEmail(savedUser.getEmail());
        userDto.setFirstName(savedUser.getFirstName());
        userDto.setLastName(savedUser.getLastName());
        userDto.setRole(savedUser.getRole().name());

        payload.setUser(userDto);

        return AuthResponse.success(payload);
    }

    @Transactional
    public AuthResponse verifyOtp(String email, String otpCode) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return AuthResponse.error("AUTH-004", "User Not Found", "No account found with this email.");
        }

        if (user.getIsVerified()) {
            return AuthResponse.error("AUTH-005", "Already Verified", "This account is already verified.");
        }

        // Check if OTP matches and hasn't expired
        if (user.getOtpCode() != null && user.getOtpCode().equals(otpCode) && user.getOtpExpiry().isAfter(LocalDateTime.now())) {

            // Success! Unlock the user and clear the OTP data
            user.setIsVerified(true);
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            userRepository.save(user);

            auditService.logAction("USER_VERIFY", "User verified email via OTP: " + user.getEmail(), user);

            return buildTokenResponse(user);
        } else {
            return AuthResponse.error("AUTH-006", "Invalid Code", "The OTP is incorrect or has expired.");
        }
    }

    @Transactional
    public AuthResponse resendOtp(String email) {
        // Find the user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Don't send OTPs to users who are already verified
        if (user.getIsVerified()) {
            return AuthResponse.error(
                    "AUTH-400",
                    "Already Verified",
                    "This account is already verified. Please log in."
            );
        }

        // Generate a new OTP and reset the 10-minute clock
        String generatedOtp = String.format("%06d", new Random().nextInt(999999));
        user.setOtpCode(generatedOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);

        // Send the beautiful HTML email again
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), generatedOtp);

        return AuthResponse.success(null);
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