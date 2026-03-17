package com.buy01.media_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j; // <-- Add this

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.File; // <-- Add this

import javax.imageio.ImageIO;

@Service
@Slf4j // <-- Add this for logging
public class LocalFileStorageService {

    private final Path fileStorageLocation;

    public LocalFileStorageService(@Value("${media.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 1. Basic Content-Type Check
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file format. Only images are allowed.");
        }

        // 2. Deep Sniffing (Verify it's actually an image)
        try (InputStream input = file.getInputStream()) {
            if (ImageIO.read(input) == null) {
                throw new IllegalArgumentException("The file is corrupt or not a valid image.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read the image file.", e);
        }

        // 3. Normalize and Save
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            if (uniqueFileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + uniqueFileName);
            }
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + uniqueFileName + ". Please try again!", ex);
        }
    }

    public Path loadFileAsPath(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }

    // ==========================================
    // NEW METHOD: Delete File
    // ==========================================
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            File file = filePath.toFile();
            
            if (file.exists()) {
                return file.delete();
            } else {
                log.warn("⚠️ File not found, could not delete: {}", filePath);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Error occurred while trying to delete file: {}", fileName, e);
            return false;
        }
    }
}