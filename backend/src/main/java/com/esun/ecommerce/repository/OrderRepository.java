package com.esun.ecommerce.repository;

import com.esun.ecommerce.dto.order.OrderDetailView;
import com.esun.ecommerce.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderRepository {

    private static final RowMapper<OrderDetailView> ORDER_DETAIL_ROW_MAPPER = OrderRepository::mapOrderDetail;
    private static final RowMapper<OrderSummary> ORDER_SUMMARY_ROW_MAPPER = OrderRepository::mapOrderSummary;

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String insertOrder(String memberId, java.math.BigDecimal totalPrice, int payStatus) {
        return jdbcTemplate.queryForObject("CALL sp_insert_order(?, ?, ?)", String.class, memberId, totalPrice, payStatus);
    }

    public void insertOrderDetail(String orderId, Product product, int quantity, java.math.BigDecimal itemPrice) {
        jdbcTemplate.update("CALL sp_insert_order_detail(?, ?, ?, ?, ?)",
                orderId,
                product.productId(),
                quantity,
                product.price(),
                itemPrice);
    }

    public int decreaseProductStock(String productId, int quantity) {
        Integer affected = jdbcTemplate.queryForObject("CALL sp_decrease_product_stock(?, ?)", Integer.class, productId, quantity);
        return affected == null ? 0 : affected;
    }

    public OrderSummary loadOrderSummary(String orderId) {
        return jdbcTemplate.queryForObject("CALL sp_get_order_summary(?)", ORDER_SUMMARY_ROW_MAPPER, orderId);
    }

    public List<OrderSummary> loadOrderSummariesByMemberId(String memberId) {
        return jdbcTemplate.query(
                "SELECT order_id, member_id, price, pay_status FROM orders WHERE member_id = ? ORDER BY created_at DESC, order_id DESC",
                ORDER_SUMMARY_ROW_MAPPER,
                memberId
        );
    }

    public List<OrderDetailView> loadOrderDetails(String orderId) {
        return jdbcTemplate.query("CALL sp_get_order_details(?)", ORDER_DETAIL_ROW_MAPPER, orderId);
    }

    private static OrderSummary mapOrderSummary(ResultSet rs, int rowNum) throws SQLException {
        return new OrderSummary(
                rs.getString("order_id"),
                rs.getString("member_id"),
                rs.getBigDecimal("price"),
                rs.getInt("pay_status")
        );
    }

    private static OrderDetailView mapOrderDetail(ResultSet rs, int rowNum) throws SQLException {
        return new OrderDetailView(
                rs.getString("product_id"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getBigDecimal("stand_price"),
                rs.getBigDecimal("item_price")
        );
    }

    public record OrderSummary(String orderId, String memberId, java.math.BigDecimal totalPrice, int payStatus) {
    }
}