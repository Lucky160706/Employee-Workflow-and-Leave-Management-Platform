-- "GENERATED ALWAYS AS IDENTITY" (chuan SQL:2003) thay vi AUTO_INCREMENT (chi H2/MySQL)
-- de 1 file schema.sql nay chay dung tren ca H2 (dev) va PostgreSQL (docker) - khong
-- can 2 ban schema rieng cho 2 database.
CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    manager_id BIGINT NULL REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    requester_id BIGINT NOT NULL REFERENCES users(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    decided_by BIGINT NULL REFERENCES users(id),
    decided_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
