package com.allra.shop_backend.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    public List<Product> findByStockGreaterThan(int stock);
}
