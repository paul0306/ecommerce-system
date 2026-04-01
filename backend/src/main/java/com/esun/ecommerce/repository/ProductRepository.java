package com.esun.ecommerce.repository;

import com.esun.ecommerce.dto.product.ProductCreateRequest;
import com.esun.ecommerce.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Repository
public class ProductRepository {

    private static final RowMapper<Product> PRODUCT_ROW_MAPPER = ProductRepository::mapProduct;

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(ProductCreateRequest request) {
        jdbcTemplate.update("CALL sp_create_product(?, ?, ?, ?)",
                request.productId(),
                request.productName(),
                request.price(),
                request.quantity());
    }

    public List<Product> findAvailableProducts() {
        return jdbcTemplate.query("CALL sp_get_available_products()", PRODUCT_ROW_MAPPER);
    }

    public List<Product> findByIds(Set<String> productIds) {
        if (productIds.isEmpty()) {
            return List.of();
        }
        TreeSet<String> sortedIds = new TreeSet<>(productIds);
        String placeholders = String.join(",", java.util.Collections.nCopies(sortedIds.size(), "?"));
        return jdbcTemplate.query(
                "SELECT product_id, product_name, price, quantity FROM product WHERE product_id IN (" + placeholders + ") ORDER BY product_id",
                PRODUCT_ROW_MAPPER,
                sortedIds.toArray()
        );
    }

    private static Product mapProduct(ResultSet rs, int rowNum) throws SQLException {
        return new Product(
                rs.getString("product_id"),
                rs.getString("product_name"),
                rs.getBigDecimal("price"),
                rs.getInt("quantity")
        );
    }
}