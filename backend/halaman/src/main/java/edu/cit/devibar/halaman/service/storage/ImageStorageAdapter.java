package edu.cit.devibar.halaman.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageAdapter {

    // Returns the file URL or file path after saving
    String saveImage(MultipartFile file) throws Exception;

    void deleteImage(String fileUrl);
}