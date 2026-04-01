package com.esun.ecommerce.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OrderItemRequest(
        @NotBlank(message = "商品編號不可為空")
        @Pattern(regexp = "^[A-Za-z0-9_-]{1,20}$", message = "商品編號格式不正確")
        String productId,

        @Min(value = 1, message = "購買數量至少為 1")
        int quantity) {
}