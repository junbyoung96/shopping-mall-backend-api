package com.allra.shop_backend.product;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class ProductResponse {
    private List<Product> products;
    private int currentPage;
    private int showCount;
    private int totalPage;

    public ProductResponse(Page<Product> productPage) {
        this.products = productPage.getContent();
        this.currentPage = productPage.getNumber() + 1;
        this.showCount = productPage.getSize();
        this.totalPage = productPage.getTotalPages();
    }
}
