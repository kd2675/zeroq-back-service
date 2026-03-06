-- Seed data for ZeroQ (userKey-key strategy)
-- Generated: 2026-03-04

USE ZEROQ;

-- ============================================================
-- User profile projection (mapped from auth user IDs)
-- ============================================================
INSERT INTO profile_user (
    user_key, display_name, tagline, profile_color, create_date, update_date
) VALUES
    (1001, '민지', '한적한 카페를 찾는 탐색가', '#4CAF50', NOW(), NOW()),
    (1002, '준호', '핫플 탐방 전문', '#FF1744', NOW(), NOW()),
    (1003, '수아', '출근 전 빠른 자리 체크', '#2196F3', NOW(), NOW());

-- ============================================================
-- Category / Space / Location / Amenity
-- ============================================================
INSERT INTO category (
    id, name, description, icon, active, create_date, update_date
) VALUES
    (1, 'cafe', '카페', '/icons/cafe.svg', 1, NOW(), NOW()),
    (2, 'gym', '헬스장', '/icons/gym.svg', 1, NOW(), NOW()),
    (3, 'mart', '마트', '/icons/mart.svg', 1, NOW(), NOW());

INSERT INTO space (
    id, category_id, name, description, capacity, phone_number, image_url, operating_hours,
    average_rating, active, verified, owner_name, owner_contact, create_date, update_date
) VALUES
    (101, 1, '스타벅스 강남점', '강남역 인근 대형 카페', 30, '02-1111-1111', '/images/spaces/starbucks-gangnam.jpg', '08:00-22:00', 4.4, 1, 1, '홍길동', '010-1000-1000', NOW(), NOW()),
    (102, 1, '블루보틀 역삼', '조용한 좌석이 많은 카페', 24, '02-2222-2222', '/images/spaces/bluebottle-yeoksam.jpg', '09:00-21:00', 4.6, 1, 1, '김영희', '010-2000-2000', NOW(), NOW()),
    (201, 2, '핏24 강남센터', '24시간 운영 피트니스', 80, '02-3333-3333', '/images/spaces/fit24.jpg', '24H', 4.2, 1, 1, '박철수', '010-3000-3000', NOW(), NOW()),
    (301, 3, '제로마트 역삼점', '중형 생활 마트', 200, '02-4444-4444', '/images/spaces/zero-mart.jpg', '10:00-23:00', 4.0, 1, 1, '최민수', '010-4000-4000', NOW(), NOW());

INSERT INTO location (
    id, space_id, latitude, longitude, address, road_address, district, city, place_id, create_date, update_date
) VALUES
    (10001, 101, 37.497942, 127.027621, '서울 강남구 강남대로 390', '강남대로 390', '강남구', '서울', 'place-zeroq-101', NOW(), NOW()),
    (10002, 102, 37.500102, 127.036231, '서울 강남구 테헤란로 231', '테헤란로 231', '강남구', '서울', 'place-zeroq-102', NOW(), NOW()),
    (10003, 201, 37.499650, 127.031510, '서울 강남구 역삼로 110', '역삼로 110', '강남구', '서울', 'place-zeroq-201', NOW(), NOW()),
    (10004, 301, 37.501320, 127.034440, '서울 강남구 논현로 201', '논현로 201', '강남구', '서울', 'place-zeroq-301', NOW(), NOW());

INSERT INTO amenity (
    id, space_id, name, description, icon, available, create_date, update_date
) VALUES
    (1, 101, 'wifi', '무료 와이파이', '/icons/wifi.svg', 1, NOW(), NOW()),
    (2, 101, 'power', '좌석별 콘센트', '/icons/power.svg', 1, NOW(), NOW()),
    (3, 102, 'quiet-zone', '조용한 구역', '/icons/quiet.svg', 1, NOW(), NOW()),
    (4, 201, 'shower', '샤워실', '/icons/shower.svg', 1, NOW(), NOW()),
    (5, 301, 'parking', '주차 가능', '/icons/parking.svg', 1, NOW(), NOW());

-- ============================================================
-- Occupancy / Insight / Analytics
-- ============================================================
INSERT INTO occupancy_data (
    id, space_id, current_occupancy, max_capacity, occupancy_percentage, crowd_level, sensor_count,
    last_updated_timestamp, create_date, update_date
) VALUES
    (1, 101, 2, 30, 6.67, 'EMPTY', 12, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (2, 102, 19, 24, 79.17, 'HIGH', 10, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (3, 201, 42, 80, 52.50, 'MEDIUM', 18, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (4, 301, 160, 200, 80.00, 'HIGH', 20, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW());

INSERT INTO occupancy_history (
    id, space_id, occupancy_count, occupancy_percentage, crowd_level, max_capacity, note, create_date, update_date
) VALUES
    (1, 101, 1, 3.33, 'EMPTY', 30, '오전 한산 시간대', NOW(), NOW()),
    (2, 101, 3, 10.00, 'EMPTY', 30, '오전 출근 시간대', NOW(), NOW()),
    (3, 102, 21, 87.50, 'FULL', 24, '점심 피크 시간', NOW(), NOW()),
    (4, 301, 170, 85.00, 'HIGH', 200, '주말 장보기 피크', NOW(), NOW());

INSERT INTO peak_hours (
    id, space_id, day_of_week, hour_of_day, average_occupancy, peak_occupancy, data_count, note, create_date, update_date
) VALUES
    (1, 101, 5, 10, 18.0, 45.0, 24, '금요일 오전', NOW(), NOW()),
    (2, 102, 5, 13, 72.0, 94.0, 24, '금요일 점심', NOW(), NOW()),
    (3, 301, 6, 17, 78.0, 93.0, 12, '토요일 저녁', NOW(), NOW());

INSERT INTO space_insights (
    id, space_id, weekly_visitors, monthly_visitors, growth_rate, busiest_day_of_week, quietest_day_of_week,
    customer_satisfaction, total_reviews, key_insights, recommendations, create_date, update_date
) VALUES
    (1, 101, 350, 1400, 5.2, 5, 2, 4.5, 42, '오전 9~11시 한산', '오전 프로모션 강화', NOW(), NOW()),
    (2, 102, 510, 1900, 9.8, 5, 1, 4.7, 73, '점심시간 혼잡 높음', '알림 기반 분산 유도', NOW(), NOW());

INSERT INTO analytics_data (
    id, space_id, total_visitors, unique_visitors, average_stay_time, average_occupancy, peak_hour, peak_day,
    period, note, create_date, update_date
) VALUES
    (1, 101, 1500, 920, 46.3, 31.1, 11, 5, 'monthly', '월간 리포트', NOW(), NOW()),
    (2, 102, 2100, 1200, 38.7, 67.8, 13, 5, 'monthly', '월간 리포트', NOW(), NOW());

-- ============================================================
-- Sensor / Attachment / NFC / Battery
-- ============================================================
INSERT INTO sensor (
    id, sensor_id, mac_address, type, model, description, battery_percentage, firmware_version,
    active, verified, location, last_heartbeat, data_count, create_date, update_date
) VALUES
    (1, 'SEN-101-01', 'AA:BB:CC:00:10:01', 'OCCUPANCY_DETECTION', 'HC-SR04', '입구 카운팅 센서', 88.0, '1.0.3', 1, 1, '입구', UNIX_TIMESTAMP(NOW()) * 1000, 5230, NOW(), NOW()),
    (2, 'SEN-102-01', 'AA:BB:CC:00:10:02', 'OCCUPANCY_DETECTION', 'HC-SR04', '좌석 존 센서', 42.0, '1.0.3', 1, 1, '좌석존A', UNIX_TIMESTAMP(NOW()) * 1000, 4120, NOW(), NOW()),
    (3, 'SEN-301-01', 'AA:BB:CC:00:30:01', 'OCCUPANCY_DETECTION', 'HC-SR04', '마트 카트 존 센서', 19.0, '1.0.1', 1, 1, '카트 보관구역', UNIX_TIMESTAMP(NOW()) * 1000, 9910, NOW(), NOW());

INSERT INTO sensor_attachment (
    id, sensor_id, space_id, attached_at, detached_at, attachment_location, active, note, create_date, update_date
) VALUES
    (1, 1, 101, NOW(), NULL, '입구 천장', 1, '운영중', NOW(), NOW()),
    (2, 2, 102, NOW(), NULL, '중앙 좌석 상단', 1, '운영중', NOW(), NOW()),
    (3, 3, 301, NOW(), NULL, '카트 라인 상단', 1, '운영중', NOW(), NOW());

INSERT INTO nfc_tag (
    id, nfc_id, space_id, location, active, read_count, last_read_timestamp, note, create_date, update_date
) VALUES
    (1, 'NFC-101-A', 101, '카운터', 1, 120, UNIX_TIMESTAMP(NOW()) * 1000, '정상', NOW(), NOW()),
    (2, 'NFC-102-A', 102, '입구', 1, 86, UNIX_TIMESTAMP(NOW()) * 1000, '정상', NOW(), NOW());

INSERT INTO battery_status (
    id, sensor_id, current_percentage, estimated_days_remaining, level, last_measured_timestamp, alert_sent, create_date, update_date
) VALUES
    (1, 1, 88.0, 48, 'FULL', UNIX_TIMESTAMP(NOW()) * 1000, 0, NOW(), NOW()),
    (2, 2, 42.0, 19, 'LOW', UNIX_TIMESTAMP(NOW()) * 1000, 0, NOW(), NOW()),
    (3, 3, 19.0, 5, 'CRITICAL', UNIX_TIMESTAMP(NOW()) * 1000, 1, NOW(), NOW());

INSERT INTO battery_history (
    id, sensor_id, percentage, level, estimated_days_remaining, note, create_date, update_date
) VALUES
    (1, 1, 89.0, 'FULL', 49, '전일 대비 -1%', NOW(), NOW()),
    (2, 2, 44.0, 'LOW', 20, '전일 대비 -2%', NOW(), NOW()),
    (3, 3, 21.0, 'LOW', 6, '저전량 경고 임박', NOW(), NOW());

INSERT INTO low_battery_alert (
    id, sensor_id, battery_percentage, level, acknowledged, acknowledged_at, note, create_date, update_date
) VALUES
    (1, 3, 19.0, 'CRITICAL', 0, NULL, '교체 필요', NOW(), NOW());

-- ============================================================
-- UserKey-key domain data
-- ============================================================
INSERT INTO favorite (
    id, user_key, space_id, order_num, note, create_date, update_date
) VALUES
    (1, 1001, 101, 1, '출근 전 체크용', NOW(), NOW()),
    (2, 1001, 102, 2, '회의 전 확인', NOW(), NOW()),
    (3, 1002, 102, 1, '핫플 체크', NOW(), NOW());

INSERT INTO review (
    id, space_id, user_key, rating, title, content, like_count, verified, deleted, admin_reply, create_date, update_date
) VALUES
    (1, 101, 1001, 5, '아침 시간 최고', '오전 10시 이전에 가면 정말 여유롭습니다.', 3, 1, 0, NULL, NOW(), NOW()),
    (2, 102, 1002, 4, '점심엔 붐빔', '좌석은 좋지만 점심 시간대는 대기 필요.', 8, 1, 0, '혼잡 시간 안내 강화 예정입니다.', NOW(), NOW());

INSERT INTO user_location (
    id, user_key, space_id, visited_at, left_at, duration_minutes, latitude, longitude, note, create_date, update_date
) VALUES
    (1, 1001, 101, NOW(), NOW(), 55, 37.497942, 127.027621, '업무 미팅', NOW(), NOW()),
    (2, 1002, 102, NOW(), NOW(), 40, 37.500102, 127.036231, '점심 방문', NOW(), NOW());

INSERT INTO user_behavior (
    id, user_key, total_visits, favorites_count, reviews_count, preferred_time_hour,
    preferred_category_id, average_occupancy_preference, note, create_date, update_date
) VALUES
    (1, 1001, 42, 2, 1, 10, 'cafe', 22.0, '한산 시간 선호', NOW(), NOW()),
    (2, 1002, 31, 1, 1, 13, 'cafe', 71.0, '핫플 선호', NOW(), NOW());

INSERT INTO user_preference (
    id, user_key, preference_key, preference_value, description, create_date, update_date
) VALUES
    (1, 1001, 'theme', 'light', '기본 테마', NOW(), NOW()),
    (2, 1001, 'distance_unit', 'km', '거리 단위', NOW(), NOW()),
    (3, 1002, 'language', 'ko', '표시 언어', NOW(), NOW()),
    (4, 1003, 'notification_enabled', 'true', '알림 사용', NOW(), NOW());

INSERT INTO notification_preference (
    id, user_key, all_notifications_enabled, occupancy_notifications_enabled, battery_notifications_enabled,
    review_notifications_enabled, promotion_notifications_enabled, email_notifications_enabled,
    push_notifications_enabled, sms_notifications_enabled, create_date, update_date
) VALUES
    (1, 1001, 1, 1, 1, 1, 0, 0, 1, 0, NOW(), NOW()),
    (2, 1002, 1, 1, 1, 1, 1, 0, 1, 0, NOW(), NOW()),
    (3, 1003, 1, 1, 0, 1, 1, 1, 1, 0, NOW(), NOW());

INSERT INTO notification (
    id, user_key, title, message, type, read_yn, related_entity_id, related_entity_type, create_date, update_date
) VALUES
    (1, 1001, 'ZeroQ 알림', '스타벅스 강남점이 ZeroQ 상태입니다.', 'SPACE_OCCUPANCY', 0, '101', 'SPACE', NOW(), NOW()),
    (2, 1002, 'HotQ 알림', '블루보틀 역삼점이 혼잡 상태입니다.', 'SPACE_OCCUPANCY', 0, '102', 'SPACE', NOW(), NOW()),
    (3, 1003, '저전량 경고', 'SEN-301-01 배터리가 위험 수준입니다.', 'LOW_BATTERY', 0, '3', 'SENSOR', NOW(), NOW());
