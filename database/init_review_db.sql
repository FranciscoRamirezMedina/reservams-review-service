CREATE DATABASE IF NOT EXISTS reservams_review_db;

USE reservams_review_db;

CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_user_id BIGINT NOT NULL,
    hotel_id BIGINT NOT NULL,
    reservation_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);