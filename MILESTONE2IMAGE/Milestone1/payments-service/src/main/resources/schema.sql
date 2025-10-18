-- Before
-- CREATE TABLE payments ( ... );

-- After
CREATE TABLE IF NOT EXISTS payments (
                                        id SERIAL PRIMARY KEY,
                                        payment_id VARCHAR(255) NOT NULL,
    amount VARCHAR(255) NOT NULL,
    method VARCHAR(100) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    payment_date DATE NOT NULL
    );