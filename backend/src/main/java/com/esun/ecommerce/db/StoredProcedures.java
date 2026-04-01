package com.esun.ecommerce.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class StoredProcedures {

    private static final DateTimeFormatter ORDER_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private StoredProcedures() {
    }

    public static int createProduct(Connection connection, String productId, String productName,
                                    BigDecimal price, Integer quantity) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO product (product_id, product_name, price, quantity, created_at) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, productId);
            statement.setString(2, productName);
            statement.setBigDecimal(3, price);
            statement.setInt(4, quantity);
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            return statement.executeUpdate();
        }
    }

    public static ResultSet getAvailableProducts(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT product_id, product_name, price, quantity FROM product WHERE quantity > 0 ORDER BY product_id");
        return statement.executeQuery();
    }

    public static ResultSet getProductsByIds(Connection connection, String idsCsv) throws SQLException {
        String[] ids = idsCsv.split(",");
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.length, "?"));
        PreparedStatement statement = connection.prepareStatement(
                "SELECT product_id, product_name, price, quantity FROM product WHERE product_id IN (" + placeholders + ") ORDER BY product_id");
        for (int i = 0; i < ids.length; i++) {
            statement.setString(i + 1, ids[i].trim());
        }
        return statement.executeQuery();
    }

    public static String insertOrder(Connection connection, String memberId, BigDecimal totalPrice, Integer payStatus)
            throws SQLException {
        String orderId = generateOrderId();
        while (existsOrder(connection, orderId)) {
            orderId = generateOrderId();
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO orders (order_id, member_id, price, pay_status, created_at) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, orderId);
            statement.setString(2, memberId);
            statement.setBigDecimal(3, totalPrice);
            statement.setInt(4, payStatus);
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
        }
        return orderId;
    }

    public static int insertOrderDetail(Connection connection, String orderId, String productId, Integer quantity,
                                        BigDecimal standPrice, BigDecimal itemPrice) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO order_detail (order_id, product_id, quantity, stand_price, item_price) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, orderId);
            statement.setString(2, productId);
            statement.setInt(3, quantity);
            statement.setBigDecimal(4, standPrice);
            statement.setBigDecimal(5, itemPrice);
            return statement.executeUpdate();
        }
    }

    public static int decreaseProductStock(Connection connection, String productId, Integer quantity) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE product SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?")) {
            statement.setInt(1, quantity);
            statement.setString(2, productId);
            statement.setInt(3, quantity);
            return statement.executeUpdate();
        }
    }

    public static ResultSet getOrderSummary(Connection connection, String orderId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT order_id, member_id, price, pay_status FROM orders WHERE order_id = ?");
        statement.setString(1, orderId);
        return statement.executeQuery();
    }

    public static ResultSet getOrderDetails(Connection connection, String orderId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT od.product_id, p.product_name, od.quantity, od.stand_price, od.item_price " +
                        "FROM order_detail od JOIN product p ON od.product_id = p.product_id " +
                        "WHERE od.order_id = ? ORDER BY od.order_item_sn");
        statement.setString(1, orderId);
        return statement.executeQuery();
    }

    private static String generateOrderId() {
        return "Ms" + LocalDateTime.now().format(ORDER_ID_FORMATTER);
    }

    private static boolean existsOrder(Connection connection, String orderId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM orders WHERE order_id = ?")) {
            statement.setString(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }
}
