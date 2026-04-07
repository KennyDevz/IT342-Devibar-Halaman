package edu.cit.devibar.halaman.service.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalImageStorageAdapter implements ImageStorageAdapter {
    private final String uploadDirectory = "uploads/";

    @Override
    public String saveImage(MultipartFile file) throws Exception {
        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return "http://localhost:8080/uploads/" + fileName;
    }

    @Override
    public void deleteImage(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDirectory).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            System.err.println("Failed to delete local image: " + e.getMessage());
        }
    }
}