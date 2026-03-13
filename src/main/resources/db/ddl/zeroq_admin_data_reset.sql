-- Reset ZeroQ admin data only.
-- Safe to run multiple times.

USE ZEROQ_ADMIN;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE notification;
TRUNCATE TABLE notification_preference;
TRUNCATE TABLE user_preference;
TRUNCATE TABLE admin_profile;
TRUNCATE TABLE gateway_registry;
TRUNCATE TABLE sensor_registry;
TRUNCATE TABLE occupancy_history;
TRUNCATE TABLE occupancy_data;
TRUNCATE TABLE amenity;
TRUNCATE TABLE location;
TRUNCATE TABLE space;

SET FOREIGN_KEY_CHECKS = 1;
