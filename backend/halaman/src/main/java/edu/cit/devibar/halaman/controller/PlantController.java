package edu.cit.devibar.halaman.controller;

import edu.cit.devibar.halaman.dto.AuthResponse;
import edu.cit.devibar.halaman.dto.PlantRequest;
import edu.cit.devibar.halaman.entity.Plant;
import edu.cit.devibar.halaman.entity.PlantImage;
import edu.cit.devibar.halaman.entity.User;
import edu.cit.devibar.halaman.repository.PlantImageRepository;
import edu.cit.devibar.halaman.service.FileStorageService;
import edu.cit.devibar.halaman.service.PlantImageService;
import edu.cit.devibar.halaman.service.PlantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;
    private final PlantImageService plantImageService;

    public PlantController(PlantService plantService, PlantImageService plantImageService) {
        this.plantService = plantService;
        this.plantImageService = plantImageService;
    }

    // GET /api/plants
    @GetMapping
    public ResponseEntity<AuthResponse> getAllPlants(
            @AuthenticationPrincipal User user) {
        AuthResponse response = plantService.getAllPlants(user.getUserId());
        return ResponseEntity.ok(response);
    }

    // GET /api/plants/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AuthResponse> getPlant(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        AuthResponse response = plantService.getPlant(id, user.getUserId());
        HttpStatus status = response.isSuccess()
                ? HttpStatus.OK
                : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    // POST /api/plants
    @PostMapping
    public ResponseEntity<AuthResponse> createPlant(
            @Valid @RequestBody PlantRequest request,
            @AuthenticationPrincipal User user) {
        AuthResponse response = plantService.createPlant(
                request, user.getUserId());
        HttpStatus status = response.isSuccess()
                ? HttpStatus.CREATED
                : HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(response);
    }

    // PUT /api/plants/{id}
    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> updatePlant(
            @PathVariable UUID id,
            @Valid @RequestBody PlantRequest request,
            @AuthenticationPrincipal User user) {
        AuthResponse response = plantService.updatePlant(
                id, request, user.getUserId());
        HttpStatus status = response.isSuccess()
                ? HttpStatus.OK
                : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    // DELETE /api/plants/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<AuthResponse> deletePlant(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        AuthResponse response = plantService.deletePlant(
                id, user.getUserId());
        HttpStatus status = response.isSuccess()
                ? HttpStatus.OK
                : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    // POST /api/plants/{id}/images
    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadPlantImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        try {
            PlantImage savedImage = plantImageService.uploadAndSaveImage(id, file);

            // Matches SDD response format
            String responseJson = String.format(
                    "{\"success\":true, \"data\": {\"imageId\":\"%s\", \"fileUrl\":\"%s\", \"uploadedAt\":\"%s\"}}",
                    savedImage.getImageId(), savedImage.getFileUrl(), savedImage.getUploadedAt()
            );

            return ResponseEntity.ok().body(responseJson);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\":false, \"error\":{\"message\":\"Image upload failed\"}}");
        }
    }

    // GET /api/plants/{id}/images
    @GetMapping("/{id}/images")
    public ResponseEntity<?> getPlantImages(@PathVariable UUID id) {
        try {
            // Ask the service for the data
            java.util.List<PlantImage> images =
                    plantImageService.getImagesForPlant(id);

            // Map the data to safely match your SDD JSON structure
            java.util.List<java.util.Map<String, Object>> responseData = images.stream().map(img -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("imageId", img.getImageId());
                map.put("fileUrl", img.getFileUrl());
                map.put("uploadedAt", img.getUploadedAt());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(java.util.Map.of("success", true, "data", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\":false, \"error\":{\"message\":\"Failed to fetch images\"}}");
        }
    }
}