package com.buy01.media_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.buy01.media_service.service.LocalFileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private LocalFileStorageService fileStorageService;

   // CHANGED: Now accepts a List of MultipartFiles
    @PostMapping("/upload")
    public ResponseEntity<List<Map<String, String>>> uploadFiles(@RequestParam("file") List<MultipartFile> files) {
        
        List<Map<String, String>> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = fileStorageService.storeFile(file);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/media/images/")
                    .path(fileName)
                    .toUriString();

            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("fileUrl", fileDownloadUri);
            
            responses.add(response);
        }

        return ResponseEntity.ok(responses);
    }
    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName) {
        try {
            Path filePath = fileStorageService.loadFileAsPath(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Determine the content type dynamically
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        // Add caching headers! (e.g., cache for 30 days)
                        .cacheControl(CacheControl.maxAge(30, java.util.concurrent.TimeUnit.DAYS))
                        .body(resource);
            } else {
                throw new IllegalArgumentException("Could not read file: " + fileName);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error retrieving file: " + fileName, e);
        }
    }
}