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
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import edu.cit.devibar.halaman.dto.GoogleAuthRequest;
import java.util.Collections;

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

    // GOOGLE OAUTH
    public AuthResponse googleAuth(GoogleAuthRequest request) {
        try {
            // Use access token to get user info from Google
            NetHttpTransport transport = new NetHttpTransport();
            GsonFactory jsonFactory = new GsonFactory();

            // Fetch user info from Google using access token
            com.google.api.client.http.HttpRequestFactory requestFactory =
                    transport.createRequestFactory();

            com.google.api.client.http.HttpRequest httpRequest =
                    requestFactory.buildGetRequest(
                            new com.google.api.client.http.GenericUrl(
                                    "https://www.googleapis.com/oauth2/v3/userinfo?access_token="
                                            + request.getToken()
                            )
                    );

            String response = httpRequest.execute().parseAsString();

            // Parse the response
            com.google.api.client.json.JsonFactory factory = new GsonFactory();
            com.google.api.client.util.GenericData userData =
                    factory.fromString(response, com.google.api.client.util.GenericData.class);

            String googleId  = (String) userData.get("sub");
            String email     = (String) userData.get("email");
            String firstName = (String) userData.get("given_name");
            String lastName  = (String) userData.get("family_name");

            if (email == null) {
                return AuthResponse.error(
                        "AUTH-003",
                        "Could not retrieve email from Google",
                        "Please make sure your Google account has an email"
                );
            }

            // Check if user already exists
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // New user — create account automatically
                user = new User();
                user.setEmail(email);
                user.setFirstName(firstName != null ? firstName : "");
                user.setLastName(lastName != null ? lastName : "");
                user.setGoogleId(googleId);
                user.setPasswordHash("");
                user.setRole(User.Role.USER);
                userRepository.save(user);
            } else {
                // Existing user — update google_id if not set
                if (user.getGoogleId() == null) {
                    user.setGoogleId(googleId);
                    userRepository.save(user);
                }
            }

            return buildTokenResponse(user);

        } catch (Exception e) {
            return AuthResponse.error(
                    "AUTH-003",
                    "Google authentication failed",
                    e.getMessage()
            );
        }
    }
}