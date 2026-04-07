package edu.cit.devibar.halaman.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ApiResponseFactory {

    public static ResponseEntity<Map<String, Object>> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<Map<String, Object>> error(HttpStatus status, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", errorMessage);
        response.put("error", errorDetails);

        return ResponseEntity.status(status).body(response);
    }
}