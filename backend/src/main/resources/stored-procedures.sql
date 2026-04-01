CREATE ALIAS IF NOT EXISTS sp_create_product FOR "com.esun.ecommerce.db.StoredProcedures.createProduct";
CREATE ALIAS IF NOT EXISTS sp_get_available_products FOR "com.esun.ecommerce.db.StoredProcedures.getAvailableProducts";
CREATE ALIAS IF NOT EXISTS sp_get_products_by_ids FOR "com.esun.ecommerce.db.StoredProcedures.getProductsByIds";
CREATE ALIAS IF NOT EXISTS sp_insert_order FOR "com.esun.ecommerce.db.StoredProcedures.insertOrder";
CREATE ALIAS IF NOT EXISTS sp_insert_order_detail FOR "com.esun.ecommerce.db.StoredProcedures.insertOrderDetail";
CREATE ALIAS IF NOT EXISTS sp_decrease_product_stock FOR "com.esun.ecommerce.db.StoredProcedures.decreaseProductStock";
CREATE ALIAS IF NOT EXISTS sp_get_order_summary FOR "com.esun.ecommerce.db.StoredProcedures.getOrderSummary";
CREATE ALIAS IF NOT EXISTS sp_get_order_details FOR "com.esun.ecommerce.db.StoredProcedures.getOrderDetails";

