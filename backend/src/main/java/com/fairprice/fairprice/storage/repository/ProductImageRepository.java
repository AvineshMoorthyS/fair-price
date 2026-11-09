package com.fairprice.fairprice.storage.repository;

import com.fairprice.fairprice.storage.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
