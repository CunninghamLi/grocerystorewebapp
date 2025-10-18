CREATE TABLE IF NOT EXISTS orders (
                                      id SERIAL PRIMARY KEY,
                                      order_id VARCHAR(36) UNIQUE,
    customer_id INT,
    payment_id INT,
    created_at TIMESTAMP,
    status VARCHAR(50),
    total_price DECIMAL(10, 2)
    );

-- Create order_items table (groceries instead of books)
CREATE TABLE IF NOT EXISTS order_items (
                                           order_id INT,
                                           product_id INT,
                                           quantity INT,
                                           FOREIGN KEY (order_id) REFERENCES orders(id)
    );
