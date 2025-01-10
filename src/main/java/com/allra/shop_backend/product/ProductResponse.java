package com.allra.shop_backend.product;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProductResponse(
        List<Product> products,
        int currentPage,
        int showCount,
        int totalPage
) {
    public ProductResponse(Page<Product> productPage) {
        this(
                productPage.getContent(),
                productPage.getNumber() + 1,
                productPage.getSize(),
                productPage.getTotalPages()
        );
    }
}
