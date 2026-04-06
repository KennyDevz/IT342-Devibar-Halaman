package edu.cit.devibar.halaman.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

public class AuthResponse {

    private boolean success;
    private DataPayload data;
    private ErrorPayload error;
    private String timestamp;


    // --- Inner classes ---
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataPayload {
        private UserDto user;
        private String accessToken;
        private String refreshToken;
        private PlantResponse plant;
        private List<PlantResponse> plants;

        // Getters
        public UserDto getUser()       { return user; }
        public String getAccessToken()    { return accessToken; }
        public String getRefreshToken()   { return refreshToken; }
        public PlantResponse getPlant()              { return plant; }
        public List<PlantResponse> getPlants()       { return plants; }

        // Setters
        public void setUser(UserDto user)             { this.user = user; }
        public void setAccessToken(String accessToken){ this.accessToken = accessToken; }
        public void setRefreshToken(String token)     { this.refreshToken = token; }
        public void setPlant(PlantResponse plant)    { this.plant = plant; }
        public void setPlants(List<PlantResponse> plants) { this.plants = plants; }
    }

    public static class UserDto {
        private String userId;
        private String email;
        private String firstName;
        private String lastName;
        private String role;

        // Getters
        public String getUserId()    { return userId; }
        public String getEmail()     { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName()  { return lastName; }
        public String getRole()      { return role; }

        // Setters
        public void setUserId(String userId)       { this.userId = userId; }
        public void setEmail(String email)         { this.email = email; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public void setLastName(String lastName)   { this.lastName = lastName; }
        public void setRole(String role)           { this.role = role; }
    }

    public static class ErrorPayload {
        private String code;
        private String message;
        private Object details;

        // Getters
        public String getCode()    { return code; }
        public String getMessage() { return message; }
        public Object getDetails() { return details; }

        // Setters
        public void setCode(String code)       { this.code = code; }
        public void setMessage(String message) { this.message = message; }
        public void setDetails(Object details) { this.details = details; }
    }

    // --- Getters ---
    public boolean isSuccess()       { return success; }
    public DataPayload getData()     { return data; }
    public ErrorPayload getError()   { return error; }
    public String getTimestamp()     { return timestamp; }

    // --- Setters ---
    public void setSuccess(boolean success)    { this.success = success; }
    public void setData(DataPayload data)      { this.data = data; }
    public void setError(ErrorPayload error)   { this.error = error; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    // --- Static factory methods ---
    public static AuthResponse success(DataPayload data) {
        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now().toString());
        return response;
    }

    public static AuthResponse error(String code, String message, Object details) {
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setCode(code);
        errorPayload.setMessage(message);
        errorPayload.setDetails(details);

        AuthResponse response = new AuthResponse();
        response.setSuccess(false);
        response.setError(errorPayload);
        response.setTimestamp(LocalDateTime.now().toString());
        return response;
    }
}