-- ZeroQ unified DDL (userKey-key strategy, muse-style resource layout)
-- Generated: 2026-03-04

DROP SCHEMA IF EXISTS ZEROQ;
CREATE SCHEMA ZEROQ;
USE ZEROQ;

-- ============================================================
-- User profile (domain-local projection of auth user)
-- ============================================================
CREATE TABLE IF NOT EXISTS profile_user (
    user_key VARCHAR(64) PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    tagline VARCHAR(255) NULL,
    profile_color VARCHAR(20) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Space / Category / Location / Amenity
-- ============================================================
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(500) NULL,
    icon VARCHAR(255) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_category_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS space (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000) NULL,
    capacity INT NOT NULL DEFAULT 0,
    phone_number VARCHAR(100) NULL,
    image_url VARCHAR(1000) NULL,
    operating_hours VARCHAR(500) NULL,
    average_rating DOUBLE NOT NULL DEFAULT 0.0,
    active TINYINT(1) NOT NULL DEFAULT 1,
    verified TINYINT(1) NOT NULL DEFAULT 0,
    owner_name VARCHAR(50) NULL,
    owner_contact VARCHAR(100) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_space_category FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_space_category_id ON space (category_id);
CREATE INDEX idx_space_name ON space (name);

CREATE TABLE IF NOT EXISTS location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    address VARCHAR(500) NOT NULL,
    road_address VARCHAR(100) NULL,
    district VARCHAR(100) NULL,
    city VARCHAR(100) NULL,
    place_id VARCHAR(255) NOT NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_location_space UNIQUE (space_id),
    CONSTRAINT fk_location_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_location_lat_lng ON location (latitude, longitude);

CREATE TABLE IF NOT EXISTS amenity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(500) NULL,
    icon VARCHAR(255) NOT NULL,
    available TINYINT(1) NOT NULL DEFAULT 1,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_amenity_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Sensor / Attachment / NFC / Battery
-- ============================================================
CREATE TABLE IF NOT EXISTS sensor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_id VARCHAR(50) NOT NULL,
    mac_address VARCHAR(20) NOT NULL,
    type VARCHAR(50) NOT NULL,
    model VARCHAR(255) NOT NULL,
    description VARCHAR(500) NULL,
    battery_percentage DOUBLE NOT NULL DEFAULT 0.0,
    firmware_version VARCHAR(20) NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    verified TINYINT(1) NOT NULL DEFAULT 0,
    location VARCHAR(100) NULL,
    last_heartbeat BIGINT NOT NULL DEFAULT 0,
    data_count INT NOT NULL DEFAULT 0,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_sensor_sensor_id UNIQUE (sensor_id),
    CONSTRAINT uk_sensor_mac_address UNIQUE (mac_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_sensor_sensor_id ON sensor (sensor_id);
CREATE INDEX idx_sensor_mac_address ON sensor (mac_address);

CREATE TABLE IF NOT EXISTS sensor_attachment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_id BIGINT NOT NULL,
    space_id BIGINT NOT NULL,
    attached_at DATETIME NOT NULL,
    detached_at DATETIME NULL,
    attachment_location VARCHAR(100) NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_sensor_attachment_sensor FOREIGN KEY (sensor_id) REFERENCES sensor(id),
    CONSTRAINT fk_sensor_attachment_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_sensor_attachment_sensor_space ON sensor_attachment (sensor_id, space_id);
CREATE INDEX idx_sensor_attachment_attached_at ON sensor_attachment (attached_at);

CREATE TABLE IF NOT EXISTS nfc_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nfc_id VARCHAR(50) NOT NULL,
    space_id BIGINT NULL,
    location VARCHAR(100) NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    read_count INT NOT NULL DEFAULT 0,
    last_read_timestamp BIGINT NOT NULL DEFAULT 0,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_nfc_tag_nfc_id UNIQUE (nfc_id),
    CONSTRAINT fk_nfc_tag_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_nfc_tag_nfc_id ON nfc_tag (nfc_id);
CREATE INDEX idx_nfc_tag_space_id ON nfc_tag (space_id);

CREATE TABLE IF NOT EXISTS battery_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_id BIGINT NOT NULL,
    current_percentage DOUBLE NOT NULL DEFAULT 0.0,
    estimated_days_remaining INT NOT NULL DEFAULT 0,
    level VARCHAR(20) NOT NULL,
    last_measured_timestamp BIGINT NOT NULL DEFAULT 0,
    alert_sent TINYINT(1) NOT NULL DEFAULT 0,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_battery_status_sensor UNIQUE (sensor_id),
    CONSTRAINT fk_battery_status_sensor FOREIGN KEY (sensor_id) REFERENCES sensor(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_battery_status_sensor_id ON battery_status (sensor_id);

CREATE TABLE IF NOT EXISTS battery_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_id BIGINT NOT NULL,
    percentage DOUBLE NOT NULL DEFAULT 0.0,
    level VARCHAR(20) NOT NULL,
    estimated_days_remaining INT NOT NULL,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_battery_history_sensor FOREIGN KEY (sensor_id) REFERENCES sensor(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_battery_history_sensor_created ON battery_history (sensor_id, create_date);

CREATE TABLE IF NOT EXISTS low_battery_alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_id BIGINT NOT NULL,
    battery_percentage DOUBLE NOT NULL DEFAULT 0.0,
    level VARCHAR(20) NOT NULL,
    acknowledged TINYINT(1) NOT NULL DEFAULT 0,
    acknowledged_at DATETIME NULL,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_low_battery_alert_sensor FOREIGN KEY (sensor_id) REFERENCES sensor(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_low_battery_alert_sensor ON low_battery_alert (sensor_id);

-- ============================================================
-- Occupancy / Insight / Analytics
-- ============================================================
CREATE TABLE IF NOT EXISTS occupancy_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    current_occupancy INT NOT NULL DEFAULT 0,
    max_capacity INT NOT NULL DEFAULT 100,
    occupancy_percentage DOUBLE NOT NULL DEFAULT 0.0,
    crowd_level VARCHAR(20) NOT NULL,
    sensor_count INT NOT NULL DEFAULT 0,
    last_updated_timestamp BIGINT NOT NULL DEFAULT 0,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_occupancy_data_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_occupancy_data_space_id ON occupancy_data (space_id);
CREATE INDEX idx_occupancy_data_update_date ON occupancy_data (update_date);

CREATE TABLE IF NOT EXISTS occupancy_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    occupancy_count INT NOT NULL,
    occupancy_percentage DOUBLE NOT NULL DEFAULT 0.0,
    crowd_level VARCHAR(20) NOT NULL,
    max_capacity INT NOT NULL,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_occupancy_history_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_occupancy_history_space_created ON occupancy_history (space_id, create_date);
CREATE INDEX idx_occupancy_history_created ON occupancy_history (create_date);

CREATE TABLE IF NOT EXISTS peak_hours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    day_of_week INT NOT NULL,
    hour_of_day INT NOT NULL,
    average_occupancy DOUBLE NOT NULL DEFAULT 0.0,
    peak_occupancy DOUBLE NOT NULL DEFAULT 0.0,
    data_count INT NOT NULL DEFAULT 0,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_peak_hours_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_peak_hours_space_day ON peak_hours (space_id, day_of_week);

CREATE TABLE IF NOT EXISTS space_insights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    weekly_visitors INT NOT NULL,
    monthly_visitors INT NOT NULL,
    growth_rate DOUBLE NOT NULL DEFAULT 0.0,
    busiest_day_of_week INT NOT NULL DEFAULT 0,
    quietest_day_of_week INT NOT NULL DEFAULT 0,
    customer_satisfaction DOUBLE NOT NULL DEFAULT 0.0,
    total_reviews INT NOT NULL,
    key_insights VARCHAR(500) NULL,
    recommendations VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_space_insights_space UNIQUE (space_id),
    CONSTRAINT fk_space_insights_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_space_insights_space_id ON space_insights (space_id);

CREATE TABLE IF NOT EXISTS analytics_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    total_visitors INT NOT NULL,
    unique_visitors INT NOT NULL,
    average_stay_time DOUBLE NOT NULL DEFAULT 0.0,
    average_occupancy DOUBLE NOT NULL DEFAULT 0.0,
    peak_hour INT NOT NULL DEFAULT 0,
    peak_day INT NOT NULL DEFAULT 0,
    period VARCHAR(20) NOT NULL,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_analytics_data_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_analytics_data_space_created ON analytics_data (space_id, create_date);

-- ============================================================
-- UserKey-key domain tables (muse-style)
-- NOTE:
--   - user_key is issued by auth server user table.
--   - domain tables do not keep FK to auth DB.
--   - profile_user is optional projection/read-model.
-- ============================================================
CREATE TABLE IF NOT EXISTS review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    user_key VARCHAR(64) NOT NULL,
    rating INT NOT NULL,
    title VARCHAR(1000) NOT NULL,
    content TEXT NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    verified TINYINT(1) NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    admin_reply VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_review_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_review_space_created ON review (space_id, create_date);
CREATE INDEX idx_review_user ON review (user_key);

CREATE TABLE IF NOT EXISTS favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(64) NOT NULL,
    space_id BIGINT NOT NULL,
    order_num INT NOT NULL DEFAULT 0,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_favorite_user_space UNIQUE (user_key, space_id),
    CONSTRAINT fk_favorite_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(64) NOT NULL,
    space_id BIGINT NOT NULL,
    visited_at DATETIME NOT NULL,
    left_at DATETIME NULL,
    duration_minutes INT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_user_location_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_user_location_user_space ON user_location (user_key, space_id);
CREATE INDEX idx_user_location_visited_at ON user_location (visited_at);

CREATE TABLE IF NOT EXISTS user_behavior (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(64) NOT NULL,
    total_visits INT NOT NULL,
    favorites_count INT NOT NULL,
    reviews_count INT NOT NULL,
    preferred_time_hour INT NOT NULL DEFAULT 0,
    preferred_category_id VARCHAR(100) NOT NULL,
    average_occupancy_preference DOUBLE NOT NULL DEFAULT 0.0,
    note VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_user_behavior_user UNIQUE (user_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_user_behavior_user_key ON user_behavior (user_key);

CREATE TABLE IF NOT EXISTS user_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(64) NOT NULL,
    preference_key VARCHAR(50) NOT NULL,
    preference_value VARCHAR(500) NOT NULL,
    description VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_user_preference_user_key UNIQUE (user_key, preference_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notification_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(64) NOT NULL,
    all_notifications_enabled TINYINT(1) NOT NULL DEFAULT 1,
    occupancy_notifications_enabled TINYINT(1) NOT NULL DEFAULT 1,
    battery_notifications_enabled TINYINT(1) NOT NULL DEFAULT 1,
    review_notifications_enabled TINYINT(1) NOT NULL DEFAULT 1,
    promotion_notifications_enabled TINYINT(1) NOT NULL DEFAULT 1,
    email_notifications_enabled TINYINT(1) NOT NULL DEFAULT 0,
    push_notifications_enabled TINYINT(1) NOT NULL DEFAULT 1,
    sms_notifications_enabled TINYINT(1) NOT NULL DEFAULT 0,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_notification_preference_user UNIQUE (user_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(64) NOT NULL,
    title VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(30) NOT NULL,
    read_yn TINYINT(1) NOT NULL DEFAULT 0,
    related_entity_id VARCHAR(500) NULL,
    related_entity_type VARCHAR(100) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_notification_user_created ON notification (user_key, create_date);
