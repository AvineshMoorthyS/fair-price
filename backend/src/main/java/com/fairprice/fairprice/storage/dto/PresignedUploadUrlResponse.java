package com.fairprice.fairprice.storage.dto;

public record PresignedUploadUrlResponse(
        String uploadUrl,
        String bucket,
        String key,
        long expiresInSeconds
) {
}
