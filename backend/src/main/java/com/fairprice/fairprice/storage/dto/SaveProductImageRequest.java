package com.fairprice.fairprice.storage.dto;

public record SaveProductImageRequest(
        Long userId,
        String productName,
        String productKey,
        String contentType
) {}
