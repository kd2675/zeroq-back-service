-- ZeroQ service schema
-- Generated: 2026-03-10

DROP SCHEMA IF EXISTS ZEROQ_SERVICE;
CREATE SCHEMA ZEROQ_SERVICE;
USE ZEROQ_SERVICE;

-- ============================================================
-- User-domain only
-- user_key comes from auth; profile_id is the local identifier used here.
-- Space/store/content metadata are owned by ZEROQ_ADMIN.
-- ============================================================
CREATE TABLE IF NOT EXISTS profile_user (
    profile_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(64) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    tagline VARCHAR(255) NULL,
    profile_color VARCHAR(20) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_profile_user_user_key UNIQUE (user_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    rating INT NOT NULL,
    title VARCHAR(1000) NOT NULL,
    content TEXT NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    verified TINYINT(1) NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    admin_reply VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_review_profile FOREIGN KEY (profile_id) REFERENCES profile_user(profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_review_space_created ON review (space_id, create_date);
CREATE INDEX idx_review_profile ON review (profile_id);

CREATE TABLE IF NOT EXISTS favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    space_id BIGINT NOT NULL,
    order_num INT NOT NULL DEFAULT 0,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_favorite_profile_space UNIQUE (profile_id, space_id),
    CONSTRAINT fk_favorite_profile FOREIGN KEY (profile_id) REFERENCES profile_user(profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_favorite_profile_space ON favorite (profile_id, space_id);

CREATE TABLE IF NOT EXISTS user_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    space_id BIGINT NOT NULL,
    visited_at DATETIME NOT NULL,
    left_at DATETIME NULL,
    duration_minutes INT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_user_location_profile FOREIGN KEY (profile_id) REFERENCES profile_user(profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_user_location_profile_space ON user_location (profile_id, space_id);
CREATE INDEX idx_user_location_visited_at ON user_location (visited_at);

CREATE TABLE IF NOT EXISTS user_behavior (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    total_visits INT NOT NULL,
    favorites_count INT NOT NULL,
    reviews_count INT NOT NULL,
    preferred_time_hour INT NOT NULL DEFAULT 0,
    preferred_category_id VARCHAR(100) NOT NULL,
    average_occupancy_preference DOUBLE NOT NULL DEFAULT 0.0,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_user_behavior_profile UNIQUE (profile_id),
    CONSTRAINT fk_user_behavior_profile FOREIGN KEY (profile_id) REFERENCES profile_user(profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_user_behavior_profile_id ON user_behavior (profile_id);
