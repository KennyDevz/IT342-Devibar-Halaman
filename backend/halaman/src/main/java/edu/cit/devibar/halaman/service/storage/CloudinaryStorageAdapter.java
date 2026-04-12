package edu.cit.devibar.halaman.service.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
public class CloudinaryStorageAdapter implements ImageStorageAdapter {

    private final Cloudinary cloudinary;

    public CloudinaryStorageAdapter(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String saveImage(MultipartFile file) throws Exception {
        Map<String, Object> options = new HashMap<>();

        options.put("folder", "halaman_uploads");

        options.put("resource_type", "auto");

        options.put("use_filename", false);
        options.put("unique_filename", true);

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

        return uploadResult.get("secure_url").toString();
    }

    @Override
    public void deleteImage(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }

        try {
            String publicId = extractPublicIdFromUrl(fileUrl);

            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));
            }
        } catch (Exception e) {
            System.err.println("Failed to delete file from Cloudinary: " + e.getMessage());
        }
    }

    // --- HELPER METHOD ---
    // Rips the public_id out of the full URL (e.g., https://res.cloudinary.com/.../halaman_uploads/abcde.jpg)
    private String extractPublicIdFromUrl(String fileUrl) {
        try {
            int uploadIndex = fileUrl.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String afterUpload = fileUrl.substring(uploadIndex + 8);

            // Remove the version number (e.g., "v12345/") if it exists
            if (afterUpload.matches("v\\d+/.*")) {
                afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
            }

            int lastDotIndex = afterUpload.lastIndexOf(".");
            if (lastDotIndex != -1) {
                return afterUpload.substring(0, lastDotIndex);
            }
            return afterUpload;
        } catch (Exception e) {
            return null;
        }
    }
}