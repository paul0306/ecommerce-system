package com.esun.ecommerce.dto.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        String orderId,
        String memberId,
        BigDecimal totalPrice,
        int payStatus,
        List<OrderDetailView> items) {
}