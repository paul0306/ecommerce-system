package com.esun.ecommerce.service;

import com.esun.ecommerce.dto.order.CreateOrderRequest;
import com.esun.ecommerce.dto.order.OrderItemRequest;
import com.esun.ecommerce.dto.order.OrderResponse;
import com.esun.ecommerce.model.Product;
import com.esun.ecommerce.repository.OrderRepository;
import com.esun.ecommerce.repository.ProductRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String orderId) {
        try {
            OrderRepository.OrderSummary summary = orderRepository.loadOrderSummary(orderId);
            return new OrderResponse(
                    summary.orderId(),
                    summary.memberId(),
                    summary.totalPrice(),
                    summary.payStatus(),
                    orderRepository.loadOrderDetails(orderId)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new IllegalArgumentException("查無此訂單：" + orderId);
        }
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Set<String> productIds = request.items().stream().map(OrderItemRequest::productId).collect(Collectors.toSet());
        List<Product> products = productRepository.findByIds(productIds);
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("訂單中包含不存在的商品");
        }

        Map<String, Product> productMap = products.stream().collect(Collectors.toMap(
                Product::productId,
                product -> product,
                (left, right) -> left,
                LinkedHashMap::new
        ));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemRequest item : request.items()) {
            Product product = productMap.get(item.productId());
            if (product.quantity() < item.quantity()) {
                throw new IllegalStateException("商品 " + product.productId() + " 庫存不足");
            }
            totalPrice = totalPrice.add(product.price().multiply(BigDecimal.valueOf(item.quantity())));
        }

        String orderId = orderRepository.insertOrder(request.memberId(), totalPrice, 0);
        for (OrderItemRequest item : request.items()) {
            Product product = productMap.get(item.productId());
            BigDecimal itemPrice = product.price().multiply(BigDecimal.valueOf(item.quantity()));
            orderRepository.insertOrderDetail(orderId, product, item.quantity(), itemPrice);
            if (orderRepository.decreaseProductStock(product.productId(), item.quantity()) == 0) {
                throw new IllegalStateException("商品 " + product.productId() + " 扣庫存失敗");
            }
        }

        return getOrder(orderId);
    }
}