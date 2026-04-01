package com.esun.ecommerce.controller;

import com.esun.ecommerce.common.ApiResponse;
import com.esun.ecommerce.dto.order.CreateOrderRequest;
import com.esun.ecommerce.dto.order.OrderResponse;
import com.esun.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getOrdersByMemberId(@RequestParam String memberId) {
        return ApiResponse.success("會員訂單查詢成功", orderService.getOrdersByMemberId(memberId));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable String orderId) {
        if (!StringUtils.hasText(orderId)) {
            throw new IllegalArgumentException("訂單編號不可為空");
        }
        return ApiResponse.success("訂單查詢成功", orderService.getOrder(orderId));
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success("訂單建立成功", orderService.createOrder(request));
    }
}