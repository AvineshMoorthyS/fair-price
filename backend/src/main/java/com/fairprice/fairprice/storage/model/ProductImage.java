package com.fairprice.fairprice.storage.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false, unique = true)
    private String productKey;

    private String contentType;

    @Column(nullable = false)
    private Instant createdAt;

    protected ProductImage() {
        // required by JPA
    }

    public ProductImage(Long userId, String productName, String bucket, String productKey, String contentType) {
        this.userId = userId;
        this.productName = productName;
        this.bucket = bucket;
        this.productKey = productKey;
        this.contentType = contentType;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getProductName() {
        return productName;
    }

    public String getBucket() {
        return bucket;
    }

    public String getProductKey() {
        return productKey;
    }

    public String getContentType() {
        return contentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
