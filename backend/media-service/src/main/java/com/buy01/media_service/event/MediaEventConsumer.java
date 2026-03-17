package com.buy01.media_service.event;

import com.buy01.media_service.service.LocalFileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaEventConsumer {

    // Inject the storage service to handle the physical deletion
    private final LocalFileStorageService fileStorageService;

    @KafkaListener(topics = "product-deletion-topic", groupId = "media-group")
    public void consumeProductDeletedEvent(String mediaId) {
        log.info("📥 Received Kafka event! Attempting to clean up orphaned media ID: {}", mediaId);
        
        try {
            // Call the storage service to securely delete the file
            boolean isDeleted = fileStorageService.deleteFile(mediaId);
            
            if (isDeleted) {
                log.info("✅ Successfully deleted physical file for media ID: {}", mediaId);
            } else {
                log.info("ℹ️ Physical file deletion skipped (file might have been deleted already): {}", mediaId);
            }

            // Note: If you eventually add a MongoDB Repository to the Media Service 
            // to track upload metadata, you would call `mediaRepository.deleteById(mediaId)` here.
            
        } catch (Exception e) {
            log.error("❌ Failed to process deletion for media ID: {}", mediaId, e);
        }
    }
}