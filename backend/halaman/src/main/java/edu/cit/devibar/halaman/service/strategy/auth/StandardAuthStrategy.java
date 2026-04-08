package edu.cit.devibar.halaman.service.strategy.auth;

import edu.cit.devibar.halaman.dto.LoginRequest;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.repository.UserRepository;
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
