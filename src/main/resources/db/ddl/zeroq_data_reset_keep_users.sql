-- Reset ZeroQ domain data only (auth users are stored in auth schema and remain intact).
-- Safe to run multiple times.

USE ZEROQ;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE notification;
TRUNCATE TABLE notification_preference;
TRUNCATE TABLE user_preference;
TRUNCATE TABLE user_behavior;
TRUNCATE TABLE user_location;
TRUNCATE TABLE favorite;
TRUNCATE TABLE review;
TRUNCATE TABLE profile_user;

TRUNCATE TABLE analytics_data;
TRUNCATE TABLE space_insights;
TRUNCATE TABLE peak_hours;
TRUNCATE TABLE occupancy_history;
TRUNCATE TABLE occupancy_data;

TRUNCATE TABLE low_battery_alert;
TRUNCATE TABLE battery_history;
TRUNCATE TABLE battery_status;
TRUNCATE TABLE nfc_tag;
TRUNCATE TABLE sensor_attachment;
TRUNCATE TABLE sensor;

TRUNCATE TABLE amenity;
TRUNCATE TABLE location;
TRUNCATE TABLE space;
TRUNCATE TABLE category;

SET FOREIGN_KEY_CHECKS = 1;
