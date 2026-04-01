package com.esun.ecommerce.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank(message = "商品編號不可為空")
        @Pattern(regexp = "^[A-Za-z0-9_-]{1,20}$", message = "商品編號僅允許英數字、底線與連字號")
        String productId,

        @NotBlank(message = "商品名稱不可為空")
        @Size(max = 100, message = "商品名稱長度不可超過 100")
        @Pattern(regexp = "^[^<>]*$", message = "商品名稱不可包含 HTML 特殊字元")
        String productName,

        @NotNull(message = "售價不可為空")
        @DecimalMin(value = "0.01", message = "售價必須大於 0")
        BigDecimal price,

        @NotNull(message = "庫存不可為空")
        @Min(value = 0, message = "庫存不可小於 0")
        Integer quantity) {
}