package edu.cit.devibar.halaman.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleAuthRequest {

    @NotBlank(message = "Google token is required")
    private String token;

    // Getter
    public String getToken() { return token; }

    // Setter
    public void setToken(String token) { this.token = token; }
}