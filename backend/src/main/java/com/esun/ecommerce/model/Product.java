package com.esun.ecommerce.model;

import java.math.BigDecimal;

public record Product(String productId, String productName, BigDecimal price, Integer quantity) {
}