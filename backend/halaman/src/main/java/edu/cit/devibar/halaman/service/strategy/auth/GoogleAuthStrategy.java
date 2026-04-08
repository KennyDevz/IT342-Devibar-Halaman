package edu.cit.devibar.halaman.service.strategy.auth;

import edu.cit.devibar.halaman.dto.GoogleAuthRequest;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.repository.UserRepository;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.GenericData;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GoogleAuthStrategy implements AuthStrategy {

    private final UserRepository userRepository;

    public GoogleAuthStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(String provider) {
        return "GOOGLE".equalsIgnoreCase(provider);
    }

    @Override
    @Transactional
    public User authenticate(Object req) {
        // 1. Cast the generic object to your specific Google DTO
        GoogleAuthRequest request = (GoogleAuthRequest) req;

        try {
            // 2. Initialize Google HTTP Transport & JSON Factory
            NetHttpTransport transport = new NetHttpTransport();
            GsonFactory jsonFactory = new GsonFactory();
            HttpRequestFactory requestFactory = transport.createRequestFactory();

            // 3. Fetch user info from Google using the access token
            HttpRequest httpRequest = requestFactory.buildGetRequest(
                    new GenericUrl("https://www.googleapis.com/oauth2/v3/userinfo?access_token="
                            + request.getToken())
            );

            String response = httpRequest.execute().parseAsString();

            // 4. Parse the Google Response
            JsonFactory factory = new GsonFactory();
            GenericData userData = factory.fromString(response, GenericData.class);

            String googleId  = (String) userData.get("sub");
            String email     = (String) userData.get("email");
            String firstName = (String) userData.get("given_name");
            String lastName  = (String) userData.get("family_name");

            if (email == null) {
                throw new RuntimeException("Could not retrieve email from Google account");
            }

            // 5. Apply your "Find or Create" logic
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // New user — create account automatically
                user = new User();
                user.setEmail(email);
                user.setFirstName(firstName != null ? firstName : "");
                user.setLastName(lastName != null ? lastName : "");
                user.setGoogleId(googleId);
                user.setPasswordHash(""); // OAuth users don't have a local password
                user.setRole(User.Role.USER);
                return userRepository.save(user);
            } else {
                // Existing user — update google_id if it's their first time using Google
                if (user.getGoogleId() == null) {
                    user.setGoogleId(googleId);
                    return userRepository.save(user);
                }
                return user;
            }

        } catch (Exception e) {
            throw new RuntimeException("Google OAuth verification failed: " + e.getMessage());
        }
    }
}