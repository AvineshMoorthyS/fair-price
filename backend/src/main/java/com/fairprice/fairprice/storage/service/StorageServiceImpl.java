package com.fairprice.fairprice.storage.service;

import com.fairprice.fairprice.storage.client.R2Properties;
import com.fairprice.fairprice.storage.dto.PresignedUploadUrlResponse;
import com.fairprice.fairprice.storage.dto.ProductImageResponse;
import com.fairprice.fairprice.storage.dto.SaveProductImageRequest;
import com.fairprice.fairprice.storage.model.ProductImage;
import com.fairprice.fairprice.storage.repository.ProductImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageServiceImpl.class);

    private static final String PRODUCT_IMAGES_FOLDER = "products-images";
    private static final Duration UPLOAD_URL_TTL = Duration.ofSeconds(1500);

    private final S3Presigner s3Presigner;
    private final R2Properties r2Properties;
    private final ProductImageRepository productImageRepository;

    public StorageServiceImpl(S3Presigner s3Presigner, R2Properties r2Properties, ProductImageRepository productImageRepository) {
        this.s3Presigner = s3Presigner;
        this.r2Properties = r2Properties;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public PresignedUploadUrlResponse generateProductImageUploadUrl(String fileName, String contentType) {
        boolean fileNameProvided = fileName != null && !fileName.isBlank();

        String extension = resolveExtension(fileName);
        if (fileNameProvided && extension.isEmpty()) {
            log.warn("fileName={} has no file extension; uploaded object key will be stored without one", fileName);
        }

        String sanitizedBaseName = sanitizeImageName(resolveBaseName(fileName));
        if (!fileNameProvided) {
            log.debug("No fileName provided; generating key using UUID only");
        } else if (sanitizedBaseName == null) {
            log.warn("fileName={} had no usable characters after sanitization; falling back to UUID only", fileName);
        }

        String identifier = sanitizedBaseName != null
                ? sanitizedBaseName + "-" + UUID.randomUUID()
                : UUID.randomUUID().toString();

        String key = PRODUCT_IMAGES_FOLDER + "/" + identifier + extension;
        log.debug("Building presigned PUT request for bucket={}, key={}, contentType={}, ttlSeconds={}",
                r2Properties.getBucket(), key, contentType, UPLOAD_URL_TTL.toSeconds());

        PutObjectRequest.Builder putObjectRequest = PutObjectRequest.builder()
                .bucket(r2Properties.getBucket())
                .key(key);

        if (contentType != null && !contentType.isBlank()) {
            putObjectRequest.contentType(contentType);
        }

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(UPLOAD_URL_TTL)
                .putObjectRequest(putObjectRequest.build())
                .build();

        try {
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            log.info("Generated presigned upload URL for bucket={}, key={}, ttlSeconds={}",
                    r2Properties.getBucket(), key, UPLOAD_URL_TTL.toSeconds());

            return new PresignedUploadUrlResponse(
                    presignedRequest.url().toString(),
                    r2Properties.getBucket(),
                    key,
                    UPLOAD_URL_TTL.toSeconds()
            );
        } catch (RuntimeException e) {
            log.error("Failed to generate presigned upload URL for bucket={}, key={}",
                    r2Properties.getBucket(), key, e);
            throw e;
        }
    }

    @Override
    public ProductImageResponse saveProductImage(SaveProductImageRequest request) {
        if (request.userId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        if (request.productName() == null || request.productName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productName is required");
        }
        if (request.productKey() == null || request.productKey().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productKey is required");
        }

        log.debug("Saving product image metadata: userId={}, productName={}, productKey={}",
                request.userId(), request.productName(), request.productKey());

        ProductImage productImage = new ProductImage(
                request.userId(),
                request.productName(),
                r2Properties.getBucket(),
                request.productKey(),
                request.contentType()
        );

        ProductImage saved;
        try {
            saved = productImageRepository.save(productImage);
        } catch (RuntimeException e) {
            log.error("Failed to save product image metadata for userId={}, productKey={}",
                    request.userId(), request.productKey(), e);
            throw e;
        }

        log.info("Saved product image metadata id={}, userId={}, productKey={}",
                saved.getId(), saved.getUserId(), saved.getProductKey());

        return new ProductImageResponse(
                saved.getId(),
                saved.getUserId(),
                saved.getProductName(),
                saved.getBucket(),
                saved.getProductKey(),
                saved.getContentType(),
                saved.getCreatedAt()
        );
    }

    private String resolveExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    private String resolveBaseName(String fileName) {
        if (fileName == null) {
            return null;
        }
        return fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
    }

    private String sanitizeImageName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        String sanitized = name.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9-_]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        return sanitized.isBlank() ? null : sanitized;
    }
}
