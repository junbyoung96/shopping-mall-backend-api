package com.allra.shop_backend.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    /**
     * 구매 가능한 상품 목록을 페이징하여 조회합니다.
     *
     * @param page      조회할 페이지 번호 (기본값: 1)
     * @param showCount 페이지당 표시할 상품 수 (기본값: 20)
     * @return {@link ProductResponse}
     */
    @GetMapping
    public ResponseEntity<ProductResponse> getAvailableProducts(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "20") int showCount){
        Pageable pageable = PageRequest.of(page - 1, showCount);
        Page<Product> productList = productService.getAvailableProducts(pageable);
        return ResponseEntity.ok(new ProductResponse(productList));
    }
}
