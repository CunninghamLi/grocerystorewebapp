CREATE TABLE IF NOT EXISTS product (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       product_id VARCHAR(50),
    name VARCHAR(255),
    description TEXT,
    price DOUBLE,
    stock_quantity INT
    );