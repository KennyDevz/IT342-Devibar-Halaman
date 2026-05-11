package edu.cit.devibar.halaman.features.auth;

import edu.cit.devibar.halaman.core.EmailService;
import edu.cit.devibar.halaman.features.admin.AuditService;
import edu.cit.devibar.halaman.features.auth.strategy.auth.AuthStrategy;
import edu.cit.devibar.halaman.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthStrategy authStrategy;

    @Mock
    private AuditService auditService;

    @Mock
    private EmailService emailService;

    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // Since strategies is a List, we must construct AuthService manually if we want to mock the list
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                Collections.singletonList(authStrategy),
                auditService,
                emailService
        );

        mockUser = new User();
        mockUser.setUserId(UUID.randomUUID());
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setRole(User.Role.USER);
        mockUser.setStatus("ACTIVE");
        mockUser.setIsVerified(true);
    }

    @Test
    void authenticate_ShouldReturnToken_WhenSuccess() {
        when(authStrategy.supports("LOCAL")).thenReturn(true);
        when(authStrategy.authenticate(any())).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("access_token");
        when(jwtService.generateRefreshToken(mockUser)).thenReturn("refresh_token");

        AuthResponse response = authService.authenticate("LOCAL", new Object());

        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getAccessToken());
        assertEquals("access_token", response.getData().getAccessToken());
    }

    @Test
    void authenticate_ShouldReturnError_WhenNotVerified() {
        mockUser.setIsVerified(false);
        when(authStrategy.supports("LOCAL")).thenReturn(true);
        when(authStrategy.authenticate(any())).thenReturn(mockUser);

        AuthResponse response = authService.authenticate("LOCAL", new Object());

        assertFalse(response.isSuccess());
        assertEquals("AUTH-403", response.getError().getCode());
        assertEquals("Account Not Verified", response.getError().getMessage());
    }

    @Test
    void authenticate_ShouldReturnError_WhenSuspended() {
        mockUser.setStatus("SUSPENDED");
        when(authStrategy.supports("LOCAL")).thenReturn(true);
        when(authStrategy.authenticate(any())).thenReturn(mockUser);

        AuthResponse response = authService.authenticate("LOCAL", new Object());

        assertFalse(response.isSuccess());
        assertEquals("AUTH-403", response.getError().getCode());
        assertEquals("Account Suspended", response.getError().getMessage());
    }

    @Test
    void authenticate_ShouldReturnError_WhenBadCredentials() {
        when(authStrategy.supports("LOCAL")).thenReturn(true);
        when(authStrategy.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        AuthResponse response = authService.authenticate("LOCAL", new Object());

        assertFalse(response.isSuccess());
        assertEquals("AUTH-401", response.getError().getCode());
    }

    @Test
    void register_ShouldSaveUserAndSendEmail_WhenSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setFirstName("New");
        request.setLastName("User");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        
        User savedUser = new User();
        savedUser.setUserId(UUID.randomUUID());
        savedUser.setEmail("new@example.com");
        savedUser.setRole(User.Role.USER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = authService.register(request);

        assertTrue(response.isSuccess());
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditService, times(1)).logAction(eq("USER_REGISTER"), anyString(), eq(savedUser));
        verify(emailService, times(1)).sendVerificationEmail(eq("new@example.com"), any(), anyString());
    }

    @Test
    void register_ShouldReturnError_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        AuthResponse response = authService.register(request);

        assertFalse(response.isSuccess());
        assertEquals("DB-002", response.getError().getCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyOtp_ShouldVerifyUser_WhenSuccess() {
        mockUser.setIsVerified(false);
        mockUser.setOtpCode("123456");
        mockUser.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser)).thenReturn("access_token");

        AuthResponse response = authService.verifyOtp("test@example.com", "123456");

        assertTrue(response.isSuccess());
        assertTrue(mockUser.getIsVerified());
        assertNull(mockUser.getOtpCode());
        verify(userRepository, times(1)).save(mockUser);
        verify(auditService, times(1)).logAction(eq("USER_VERIFY"), anyString(), eq(mockUser));
    }

    @Test
    void verifyOtp_ShouldReturnError_WhenCodeInvalid() {
        mockUser.setIsVerified(false);
        mockUser.setOtpCode("123456");
        mockUser.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        AuthResponse response = authService.verifyOtp("test@example.com", "000000");

        assertFalse(response.isSuccess());
        assertEquals("AUTH-006", response.getError().getCode());
        verify(userRepository, never()).save(mockUser);
    }
}
