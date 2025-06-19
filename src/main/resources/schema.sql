CREATE TABLE IF NOT EXISTS users
(
    user_id BIGSERIAL PRIMARY KEY,
    last_name varchar(100) DEFAULT NULL,
    first_name varchar(100) NOT NULL,
    middle_name varchar(100) DEFAULT NULL,
    birth_date DATE NULL,
    email VARCHAR(255) UNIQUE DEFAULT NULL,
    phone_number VARCHAR(30) DEFAULT NULL,
    photo VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);