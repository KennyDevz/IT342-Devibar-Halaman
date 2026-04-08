package edu.cit.devibar.halaman.service.strategy.auth;

import edu.cit.devibar.halaman.entity.User;

public interface AuthStrategy {
    // Defines if this strategy handles "STANDARD" or "GOOGLE"
    boolean supports(String provider);

    // The logic to verify the credentials and return the User entity
    User authenticate(Object request);
}
