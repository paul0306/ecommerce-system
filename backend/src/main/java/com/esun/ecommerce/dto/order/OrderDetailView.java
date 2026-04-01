package com.esun.ecommerce.dto.order;

import java.math.BigDecimal;

public record OrderDetailView(
        String productId,
        String productName,
        Integer quantity,
        BigDecimal standPrice,
        BigDecimal itemPrice) {
}