package com.esun.ecommerce.service;

import com.esun.ecommerce.dto.product.ProductCreateRequest;
import com.esun.ecommerce.model.Product;
import com.esun.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductCreateRequest request) {
        productRepository.create(request);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }
}