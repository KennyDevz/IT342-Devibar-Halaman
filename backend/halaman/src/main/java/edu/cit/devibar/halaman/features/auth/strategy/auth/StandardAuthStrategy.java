package edu.cit.devibar.halaman.features.auth.strategy.auth;

import edu.cit.devibar.halaman.features.auth.LoginRequest;
import edu.cit.devibar.halaman.features.auth.User;
import edu.cit.devibar.halaman.features.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class StandardAuthStrategy implements AuthStrategy {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;

    @Override
    public boolean supports(String provider) { return "STANDARD".equalsIgnoreCase(provider); }

    @Override
    public User authenticate(Object req) {
        LoginRequest request = (LoginRequest) req;
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        return userRepository.findByEmail(request.getEmail()).orElseThrow();
    }
}
