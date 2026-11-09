package com.fairprice.fairprice.storage.controller;

import com.fairprice.fairprice.storage.dto.PresignedUploadUrlResponse;
import com.fairprice.fairprice.storage.dto.SaveProductImageRequest;
import com.fairprice.fairprice.storage.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/storage/products-images")
public class StorageController {

    private static final Logger log = LoggerFactory.getLogger(StorageController.class);

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/upload-url")
    public ResponseEntity<PresignedUploadUrlResponse> getUploadUrl(
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false, defaultValue = "application/octet-stream") String contentType
    ) {
        log.debug("Received presigned upload URL request: fileName={}, contentType={}", fileName, contentType);
        PresignedUploadUrlResponse response = storageService.generateProductImageUploadUrl(fileName, contentType);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> saveProductImage(@RequestBody SaveProductImageRequest request) {
        log.debug("Received save product image request: userId={}, productName={}, productKey={}",
                request.userId(), request.productName(), request.productKey());
        storageService.saveProductImage(request);
        return ResponseEntity.noContent().build();
    }
}
