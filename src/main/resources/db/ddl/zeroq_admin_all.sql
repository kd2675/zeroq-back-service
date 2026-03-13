-- ZeroQ admin schema
-- Generated: 2026-03-10

DROP SCHEMA IF EXISTS ZEROQ_ADMIN;
CREATE SCHEMA ZEROQ_ADMIN;
USE ZEROQ_ADMIN;

-- ============================================================
-- Admin identity
-- ============================================================
CREATE TABLE IF NOT EXISTS admin_profile (
    profile_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(64) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    tagline VARCHAR(255) NULL,
    profile_color VARCHAR(20) NULL,
    role VARCHAR(20) NOT NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_admin_profile_user_key UNIQUE (user_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS space (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_profile_id BIGINT NOT NULL,
    space_code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000) NULL,
    operational_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    phone_number VARCHAR(100) NULL,
    image_url VARCHAR(1000) NULL,
    operating_hours VARCHAR(500) NULL,
    average_rating DOUBLE NOT NULL DEFAULT 0.0,
    active TINYINT(1) NOT NULL DEFAULT 1,
    verified TINYINT(1) NOT NULL DEFAULT 0,
    owner_name VARCHAR(100) NULL,
    owner_contact VARCHAR(100) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_admin_space_owner_profile FOREIGN KEY (owner_profile_id) REFERENCES admin_profile(profile_id),
    CONSTRAINT uk_admin_space_code UNIQUE (space_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_admin_space_name ON space (name);
CREATE INDEX idx_admin_space_owner_profile_id ON space (owner_profile_id);

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
    CONSTRAINT uk_admin_location_space UNIQUE (space_id),
    CONSTRAINT fk_admin_location_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_admin_location_lat_lng ON location (latitude, longitude);

CREATE TABLE IF NOT EXISTS amenity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(500) NULL,
    icon VARCHAR(255) NOT NULL,
    available TINYINT(1) NOT NULL DEFAULT 1,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_admin_amenity_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Admin-owned live occupancy summary
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
    CONSTRAINT fk_admin_occupancy_data_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_admin_occupancy_space_id ON occupancy_data (space_id);
CREATE INDEX idx_admin_occupancy_update_date ON occupancy_data (update_date);

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
    CONSTRAINT fk_admin_occupancy_history_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_admin_occ_history_space_created ON occupancy_history (space_id, create_date);
CREATE INDEX idx_admin_occ_history_created ON occupancy_history (create_date);

-- ============================================================
-- Admin-owned device registry
-- gatewayId / sensorId are the cross-service linkage keys.
-- ============================================================
CREATE TABLE IF NOT EXISTS gateway_registry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gateway_id VARCHAR(50) NOT NULL,
    space_id BIGINT NULL,
    gateway_name VARCHAR(100) NOT NULL,
    gateway_role VARCHAR(20) NOT NULL DEFAULT 'EDGE',
    region_code VARCHAR(50) NULL,
    location_label VARCHAR(255) NULL,
    ip_address VARCHAR(50) NULL,
    sensor_capacity INT NOT NULL DEFAULT 0,
    current_sensor_load INT NOT NULL DEFAULT 0,
    latency_ms INT NULL,
    packet_loss_percent DOUBLE NULL,
    last_heartbeat_at DATETIME NULL,
    status VARCHAR(20) NOT NULL,
    firmware_version VARCHAR(20) NULL,
    description VARCHAR(255) NULL,
    linked_bridge VARCHAR(100) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_admin_gateway_registry_gateway_id UNIQUE (gateway_id),
    CONSTRAINT fk_admin_gateway_registry_space FOREIGN KEY (space_id) REFERENCES space(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sensor_registry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_id VARCHAR(50) NOT NULL,
    mac_address VARCHAR(20) NOT NULL,
    model VARCHAR(100) NOT NULL,
    firmware_version VARCHAR(20) NULL,
    type VARCHAR(30) NOT NULL,
    protocol VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    gateway_id VARCHAR(50) NULL,
    position_code VARCHAR(50) NULL,
    battery_percent DOUBLE NULL,
    occupancy_threshold_cm DOUBLE NULL,
    calibration_offset_cm DOUBLE NULL,
    last_heartbeat_at DATETIME NULL,
    last_sequence_no BIGINT NULL,
    metadata_json TEXT NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_admin_sensor_registry_sensor_id UNIQUE (sensor_id),
    CONSTRAINT uk_admin_sensor_registry_mac_address UNIQUE (mac_address),
    CONSTRAINT fk_admin_sensor_registry_gateway FOREIGN KEY (gateway_id) REFERENCES gateway_registry(gateway_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_admin_sensor_gateway_status ON sensor_registry (gateway_id, status);
CREATE INDEX idx_admin_sensor_sensor_id ON sensor_registry (sensor_id);
CREATE INDEX idx_admin_sensor_gateway_id ON sensor_registry (gateway_id);

CREATE INDEX idx_admin_gateway_space_id ON gateway_registry (space_id);

-- ============================================================
-- Admin preferences / notifications
-- auth.user_key is linked to ZEROQ_ADMIN.admin_profile.
-- ============================================================
CREATE TABLE IF NOT EXISTS user_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    preference_key VARCHAR(50) NOT NULL,
    preference_value VARCHAR(500) NOT NULL,
    description VARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT uk_user_preference_profile_key UNIQUE (profile_id, preference_key),
    CONSTRAINT fk_user_preference_admin_profile FOREIGN KEY (profile_id) REFERENCES admin_profile(profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_user_preference_profile_id ON user_preference (profile_id);

CREATE TABLE IF NOT EXISTS notification_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
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
    CONSTRAINT uk_notification_preference_profile UNIQUE (profile_id),
    CONSTRAINT fk_notification_preference_admin_profile FOREIGN KEY (profile_id) REFERENCES admin_profile(profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(30) NOT NULL,
    read_yn TINYINT(1) NOT NULL DEFAULT 0,
    related_entity_id VARCHAR(500) NULL,
    related_entity_type VARCHAR(100) NULL,
    create_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    CONSTRAINT fk_notification_admin_profile FOREIGN KEY (profile_id) REFERENCES admin_profile(profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_notification_profile_created ON notification (profile_id, create_date);
