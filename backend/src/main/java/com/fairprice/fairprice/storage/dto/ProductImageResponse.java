package com.fairprice.fairprice.storage.dto;

import java.time.Instant;

public record ProductImageResponse(
        Long id,
        Long userId,
        String productName,
        String bucket,
        String productKey,
        String contentType,
        Instant createdAt
) {}
