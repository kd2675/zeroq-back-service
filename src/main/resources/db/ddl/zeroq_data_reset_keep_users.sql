-- Reset ZeroQ service user-domain data only.
-- Safe to run multiple times.

USE ZEROQ_SERVICE;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE user_behavior;
TRUNCATE TABLE user_location;
TRUNCATE TABLE favorite;
TRUNCATE TABLE review;
-- keep profile_user mapping (user_key <-> profile_id)

SET FOREIGN_KEY_CHECKS = 1;
