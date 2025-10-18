INSERT INTO orders (order_id, customer_id, payment_id, created_at, status, total_price) VALUES
                                                                                            ('g1d2b3a4-6b7c-890d-1e2f-3a4b5c6d7e8f', 1, 1, NOW(), 'Processing', 34.50),
                                                                                            ('g2d3b4a5-6b7c-890d-1e2f-3a4b5c6d7e8f', 2, 2, NOW(), 'Completed', 82.90),
                                                                                            ('g3d4b5a6-6b7c-890d-1e2f-3a4b5c6d7e8f', 3, 3, NOW(), 'Delivered', 59.99),
                                                                                            ('g4d5b6a7-6b7c-890d-1e2f-3a4b5c6d7e8f', 4, 4, NOW(), 'Cancelled', 45.20),
                                                                                            ('g5d6b7a8-6b7c-890d-1e2f-3a4b5c6d7e8f', 5, 5, NOW(), 'Processing', 120.00);

-- Insert grocery items for each order
INSERT INTO order_items (order_id, product_id, quantity) VALUES
                                                             (1, 101, 2),  -- Bananas
                                                             (1, 102, 1),  -- Milk
                                                             (2, 103, 5),  -- Eggs
                                                             (2, 104, 1),  -- Bread
                                                             (3, 105, 3),  -- Chicken Breast
                                                             (4, 106, 2),  -- Cheese
                                                             (5, 107, 2),  -- Orange Juice
                                                             (5, 108, 1),  -- Apples
                                                             (5, 109, 1);  -- Tomatoes