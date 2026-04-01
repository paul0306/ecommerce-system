package com.esun.ecommerce.controller;

import com.esun.ecommerce.common.ApiResponse;
import com.esun.ecommerce.dto.product.ProductCreateRequest;
import com.esun.ecommerce.model.Product;
import com.esun.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/available")
    public ApiResponse<List<Product>> getAvailableProducts() {
        return ApiResponse.success("查詢成功", productService.getAvailableProducts());
    }

    @PostMapping
    public ApiResponse<Void> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        productService.createProduct(request);
        return ApiResponse.success("商品新增成功");
    }
}