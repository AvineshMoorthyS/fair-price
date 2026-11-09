package com.fairprice.fairprice.storage.service;

import com.fairprice.fairprice.storage.dto.PresignedUploadUrlResponse;
import com.fairprice.fairprice.storage.dto.ProductImageResponse;
import com.fairprice.fairprice.storage.dto.SaveProductImageRequest;

public interface StorageService {

    PresignedUploadUrlResponse generateProductImageUploadUrl(String fileName, String contentType);

    ProductImageResponse saveProductImage(SaveProductImageRequest request);
}
