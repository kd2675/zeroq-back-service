-- Seed data for ZeroQ service DB (user-domain only)
-- Generated: 2026-03-10

USE ZEROQ_SERVICE;

INSERT INTO profile_user (
    profile_id, user_key, display_name, tagline, profile_color, create_date, update_date
) VALUES
    (1, '1001', '민지', '한적한 카페를 찾는 탐색가', '#4CAF50', NOW(), NOW()),
    (2, '1002', '준호', '핫플 탐방 전문', '#FF1744', NOW(), NOW()),
    (3, '1003', '수아', '출근 전 빠른 자리 체크', '#2196F3', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    display_name = VALUES(display_name),
    tagline = VALUES(tagline),
    profile_color = VALUES(profile_color),
    update_date = NOW();

INSERT INTO favorite (
    id, profile_id, space_id, order_num, note, create_date, update_date
) VALUES
    (1, 1, 101, 1, '출근 전 체크용', NOW(), NOW()),
    (2, 1, 102, 2, '회의 전 확인', NOW(), NOW()),
    (3, 2, 102, 1, '핫플 체크', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    order_num = VALUES(order_num),
    note = VALUES(note),
    update_date = NOW();

INSERT INTO review (
    id, space_id, profile_id, rating, title, content, like_count, verified, deleted, admin_reply, create_date, update_date
) VALUES
    (1, 101, 1, 5, '아침 시간 최고', '오전 10시 이전에 가면 정말 여유롭습니다.', 3, 1, 0, NULL, NOW(), NOW()),
    (2, 102, 2, 4, '점심엔 붐빔', '좌석은 좋지만 점심 시간대는 대기 필요.', 8, 1, 0, '혼잡 시간 안내 강화 예정입니다.', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    rating = VALUES(rating),
    title = VALUES(title),
    content = VALUES(content),
    like_count = VALUES(like_count),
    verified = VALUES(verified),
    deleted = VALUES(deleted),
    admin_reply = VALUES(admin_reply),
    update_date = NOW();

INSERT INTO user_location (
    id, profile_id, space_id, visited_at, left_at, duration_minutes, latitude, longitude, note, create_date, update_date
) VALUES
    (1, 1, 101, NOW(), NOW(), 55, 37.497942, 127.027621, '업무 미팅', NOW(), NOW()),
    (2, 2, 102, NOW(), NOW(), 40, 37.500102, 127.036231, '점심 방문', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    visited_at = VALUES(visited_at),
    left_at = VALUES(left_at),
    duration_minutes = VALUES(duration_minutes),
    latitude = VALUES(latitude),
    longitude = VALUES(longitude),
    note = VALUES(note),
    update_date = NOW();

INSERT INTO user_behavior (
    id, profile_id, total_visits, favorites_count, reviews_count, preferred_time_hour,
    preferred_category_id, average_occupancy_preference, note, create_date, update_date
) VALUES
    (1, 1, 12, 2, 1, 9, '1', 22.5, '한산한 오전 카페 선호', NOW(), NOW()),
    (2, 2, 18, 1, 1, 13, '1', 61.0, '점심시간 혼잡도 감수', NOW(), NOW()),
    (3, 3, 7, 0, 0, 8, '2', 35.0, '출근 전 짧은 체류 선호', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    total_visits = VALUES(total_visits),
    favorites_count = VALUES(favorites_count),
    reviews_count = VALUES(reviews_count),
    preferred_time_hour = VALUES(preferred_time_hour),
    preferred_category_id = VALUES(preferred_category_id),
    average_occupancy_preference = VALUES(average_occupancy_preference),
    note = VALUES(note),
    update_date = NOW();
