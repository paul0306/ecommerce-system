package com.esun.ecommerce.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "會員編號不可為空")
        @Size(max = 20, message = "會員編號長度不可超過 20")
        @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "會員編號僅允許英數字、底線與連字號")
        String memberId,

        @NotEmpty(message = "訂單明細不可為空")
        List<@Valid OrderItemRequest> items) {
}