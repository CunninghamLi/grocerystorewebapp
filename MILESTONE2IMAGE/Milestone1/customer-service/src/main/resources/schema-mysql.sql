-- Before
-- CREATE TABLE customers ( ... );

-- After
USE `milestone1`;

CREATE TABLE IF NOT EXISTS customers (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    street_address VARCHAR(255),
    city VARCHAR(255),
    province VARCHAR(255),
    country VARCHAR(255),
    postal_code VARCHAR(20)
    );