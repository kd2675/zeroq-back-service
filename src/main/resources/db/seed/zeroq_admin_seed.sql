-- Seed data for ZeroQ admin DB
-- Generated: 2026-03-10

USE ZEROQ_ADMIN;

-- ============================================================
-- Admin identity
-- ============================================================
INSERT INTO admin_profile (
    profile_id, user_key, display_name, tagline, profile_color, role, create_date, update_date
) VALUES
    (1, 'seed-admin-user-key-1', 'ZeroQ Admin', '실시간 공간 점유율을 관리하는 운영자', '#2B8CEE', 'ADMIN', NOW(), NOW()),
    (2, 'seed-manager-user-key-1', 'ZeroQ Manager', '매장 운영 상태를 관리하는 매니저', '#00A878', 'MANAGER', NOW(), NOW()),
    (3, 'seed-manager-user-key-2', 'ZeroQ Site Lead', '현장 센서와 공간 배치를 관리합니다.', '#E67E22', 'MANAGER', NOW(), NOW()),
    (4, 'seed-manager-user-key-4', 'ZeroQ Multi Site Manager', '여러 매장의 실시간 혼잡도와 장비 상태를 관리합니다.', '#6C5CE7', 'MANAGER', NOW(), NOW()),
    (5, 'seed-manager-user-key-5', 'ZeroQ Ops Captain', '임시 공간과 폐쇄 공간까지 포함해 운영 상태를 총괄합니다.', '#C0392B', 'MANAGER', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    display_name = VALUES(display_name),
    tagline = VALUES(tagline),
    profile_color = VALUES(profile_color),
    role = VALUES(role),
    update_date = NOW();

-- ============================================================
-- Space / location / amenities
-- ============================================================
INSERT INTO space (
    id, owner_profile_id, space_code, name, description, operational_status,
    phone_number, image_url, operating_hours, average_rating, active, verified,
    owner_name, owner_contact, create_date, update_date
) VALUES
    (101, 4, 'ZN-001-N', 'North Wing Lobby', '북측 출입구와 안내 데스크가 연결된 메인 로비', 'CRITICAL', '02-7000-1001', '/images/spaces/north-wing-lobby.jpg', '24H', 4.8, 1, 1, 'Alex Rivera', '010-7000-1001', NOW(), NOW()),
    (102, 4, 'ZN-002-D', 'Dining Hall B', '점심 피크가 집중되는 식음 공간', 'ACTIVE', '02-7000-1002', '/images/spaces/dining-hall-b.jpg', '07:00-20:00', 4.5, 1, 1, 'Alex Rivera', '010-7000-1002', NOW(), NOW()),
    (201, 4, 'ZN-003-C', 'Conference Center', '세미나와 대형 회의가 열리는 컨퍼런스 존', 'ACTIVE', '02-7000-1003', '/images/spaces/conference-center.jpg', '08:00-21:00', 4.6, 1, 1, 'Alex Rivera', '010-7000-1003', NOW(), NOW()),
    (301, 4, 'ZN-004-S', 'Staff Lounge', '직원 휴게 및 간이 회의 공간', 'ACTIVE', '02-7000-1004', '/images/spaces/staff-lounge.jpg', '24H', 4.2, 1, 1, 'Alex Rivera', '010-7000-1004', NOW(), NOW()),
    (401, 4, 'ZN-104-A', 'Meeting Room Alpha', '회의실 과밀 감지가 자주 발생하는 알파 룸', 'CRITICAL', '02-7000-1005', '/images/spaces/meeting-room-alpha.jpg', '08:00-22:00', 4.4, 1, 1, 'Alex Rivera', '010-7000-1005', NOW(), NOW()),
    (402, 4, 'ZN-201-B', 'Main Workspace', '상시 운영 인원이 가장 많은 메인 워크스페이스', 'ACTIVE', '02-7000-1006', '/images/spaces/main-workspace.jpg', '24H', 4.7, 1, 1, 'Alex Rivera', '010-7000-1006', NOW(), NOW()),
    (403, 4, 'ZN-305-C', 'Innovation Lab', '하드웨어 점검 중인 실험 랩', 'MAINTENANCE', '02-7000-1007', '/images/spaces/innovation-lab.jpg', '09:00-19:00', 4.1, 1, 1, 'Alex Rivera', '010-7000-1007', NOW(), NOW()),
    (404, 4, 'ZN-092-D', 'Coffee Lounge', '짧은 체류와 회전율이 높은 라운지 공간', 'ACTIVE', '02-7000-1008', '/images/spaces/coffee-lounge.jpg', '07:00-21:00', 4.3, 1, 1, 'Alex Rivera', '010-7000-1008', NOW(), NOW()),
    (405, 4, 'ZN-112-Q', 'Quiet Pods', '집중 업무용 저소음 포드 구역', 'ACTIVE', '02-7000-1009', '/images/spaces/quiet-pods.jpg', '24H', 4.5, 1, 1, 'Alex Rivera', '010-7000-1009', NOW(), NOW()),
    (406, 4, 'ZN-005-S', 'Storage B', '물류 적재와 임시 보관이 이루어지는 지원 공간', 'CRITICAL', '02-7000-1010', '/images/spaces/storage-b.jpg', '24H', 3.9, 1, 1, 'Alex Rivera', '010-7000-1010', NOW(), NOW()),
    (501, 2, 'ZN-410-SA', 'South Annex Workspace', '신규 인력 배치를 준비 중인 사우스 워크스페이스', 'ACTIVE', '02-7100-1001', '/images/spaces/south-annex-workspace.jpg', '08:00-22:00', 4.0, 1, 1, 'ZeroQ Manager', '010-7100-1001', NOW(), NOW()),
    (502, 2, 'ZN-411-DR', 'Expansion Draft Zone', '오픈 전 배치와 센서 설치를 준비 중인 드래프트 공간', 'STAGING', '02-7100-1002', '/images/spaces/expansion-draft-zone.jpg', '예정', 0.0, 0, 0, 'ZeroQ Manager', '010-7100-1002', NOW(), NOW()),
    (503, 2, 'ZN-412-RT', 'Rooftop Event Deck', '행사 시간에만 열리는 루프탑 이벤트 덱', 'ACTIVE', '02-7100-1003', '/images/spaces/rooftop-event-deck.jpg', '17:00-23:00', 4.6, 1, 1, 'ZeroQ Manager', '010-7100-1003', NOW(), NOW()),
    (601, 3, 'ZN-510-BS', 'Basement Support Hub', '설비 반입과 장비 적재를 담당하는 지하 지원 허브', 'MAINTENANCE', '02-7200-1001', '/images/spaces/basement-support-hub.jpg', '24H', 3.8, 1, 0, 'ZeroQ Site Lead', '010-7200-1001', NOW(), NOW()),
    (602, 3, 'ZN-511-TR', 'Training Room Delta', '정기 교육과 온보딩 세션이 열리는 트레이닝 룸', 'ACTIVE', '02-7200-1002', '/images/spaces/training-room-delta.jpg', '09:00-18:00', 4.4, 1, 1, 'ZeroQ Site Lead', '010-7200-1002', NOW(), NOW()),
    (603, 5, 'ZN-610-AR', 'Archive Vault', '폐쇄 운영 중인 문서 아카이브 보관 구역', 'CLOSED', '02-7300-1001', '/images/spaces/archive-vault.jpg', '폐쇄', 3.5, 0, 1, 'ZeroQ Ops Captain', '010-7300-1001', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    owner_profile_id = VALUES(owner_profile_id),
    space_code = VALUES(space_code),
    description = VALUES(description),
    operational_status = VALUES(operational_status),
    phone_number = VALUES(phone_number),
    image_url = VALUES(image_url),
    operating_hours = VALUES(operating_hours),
    average_rating = VALUES(average_rating),
    active = VALUES(active),
    verified = VALUES(verified),
    owner_name = VALUES(owner_name),
    owner_contact = VALUES(owner_contact),
    update_date = NOW();

INSERT INTO location (
    id, space_id, latitude, longitude, address, road_address, district, city, place_id, create_date, update_date
) VALUES
    (10001, 101, 37.498100, 127.027500, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-101', NOW(), NOW()),
    (10002, 102, 37.498200, 127.027700, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-102', NOW(), NOW()),
    (10003, 201, 37.498300, 127.027900, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-201', NOW(), NOW()),
    (10004, 301, 37.498400, 127.028100, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-301', NOW(), NOW()),
    (10005, 401, 37.498500, 127.028300, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-401', NOW(), NOW()),
    (10006, 402, 37.498600, 127.028500, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-402', NOW(), NOW()),
    (10007, 403, 37.498700, 127.028700, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-403', NOW(), NOW()),
    (10008, 404, 37.498800, 127.028900, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-404', NOW(), NOW()),
    (10009, 405, 37.498900, 127.029100, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-405', NOW(), NOW()),
    (10010, 406, 37.499000, 127.029300, '서울 강남구 테헤란로 101', '테헤란로 101', '강남구', '서울', 'place-zeroq-406', NOW(), NOW()),
    (10011, 501, 37.501100, 127.031100, '서울 강남구 언주로 210', '언주로 210', '강남구', '서울', 'place-zeroq-501', NOW(), NOW()),
    (10012, 502, 37.501300, 127.031400, '서울 강남구 언주로 210', '언주로 210', '강남구', '서울', 'place-zeroq-502', NOW(), NOW()),
    (10013, 503, 37.501500, 127.031700, '서울 강남구 언주로 210', '언주로 210', '강남구', '서울', 'place-zeroq-503', NOW(), NOW()),
    (10014, 601, 37.503100, 127.033100, '서울 송파구 올림픽로 88', '올림픽로 88', '송파구', '서울', 'place-zeroq-601', NOW(), NOW()),
    (10015, 602, 37.503300, 127.033400, '서울 송파구 올림픽로 88', '올림픽로 88', '송파구', '서울', 'place-zeroq-602', NOW(), NOW()),
    (10016, 603, 37.505100, 127.035100, '서울 중구 세종대로 55', '세종대로 55', '중구', '서울', 'place-zeroq-603', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    latitude = VALUES(latitude),
    longitude = VALUES(longitude),
    address = VALUES(address),
    road_address = VALUES(road_address),
    district = VALUES(district),
    city = VALUES(city),
    place_id = VALUES(place_id),
    update_date = NOW();

INSERT INTO amenity (
    id, space_id, name, description, icon, available, create_date, update_date
) VALUES
    (1, 101, 'reception', '방문객 안내 데스크', '/icons/reception.svg', 1, NOW(), NOW()),
    (2, 102, 'serving-line', '배식 라인', '/icons/serving-line.svg', 1, NOW(), NOW()),
    (3, 201, 'presentation', '대형 프레젠테이션 스크린', '/icons/presentation.svg', 1, NOW(), NOW()),
    (4, 301, 'coffee-bar', '셀프 커피바', '/icons/coffee.svg', 1, NOW(), NOW()),
    (5, 401, 'booking-panel', '회의실 예약 패널', '/icons/panel.svg', 1, NOW(), NOW()),
    (6, 402, 'focus-zone', '집중 좌석 구역', '/icons/focus.svg', 1, NOW(), NOW()),
    (7, 403, 'prototype-bench', '프로토타이핑 벤치', '/icons/lab.svg', 0, NOW(), NOW()),
    (8, 404, 'coffee-machine', '커피머신', '/icons/coffee.svg', 1, NOW(), NOW()),
    (9, 405, 'privacy-wall', '방음 파티션', '/icons/privacy.svg', 1, NOW(), NOW()),
    (10, 406, 'rack-system', '랙 보관 시스템', '/icons/storage.svg', 1, NOW(), NOW()),
    (11, 501, 'standing-desk', '높이 조절 데스크', '/icons/workspace.svg', 1, NOW(), NOW()),
    (12, 502, 'staging-rack', '임시 설치 랙', '/icons/storage.svg', 1, NOW(), NOW()),
    (13, 503, 'event-lighting', '행사 조명 컨트롤', '/icons/event.svg', 1, NOW(), NOW()),
    (14, 601, 'loading-bay', '반입용 적재 도크', '/icons/storage.svg', 1, NOW(), NOW()),
    (15, 602, 'training-panel', '교육용 제어 패널', '/icons/panel.svg', 1, NOW(), NOW()),
    (16, 603, 'vault-door', '통제 출입 게이트', '/icons/archive.svg', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    icon = VALUES(icon),
    available = VALUES(available),
    update_date = NOW();

-- ============================================================
-- Occupancy summary and history
-- ============================================================
INSERT INTO occupancy_data (
    id, space_id, current_occupancy, max_capacity, occupancy_percentage, crowd_level, sensor_count,
    last_updated_timestamp, create_date, update_date
) VALUES
    (1, 101, 202, 220, 91.82, 'FULL', 12, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (2, 102, 158, 180, 87.78, 'FULL', 10, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (3, 201, 90, 140, 64.29, 'MEDIUM', 8, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (4, 301, 25, 60, 41.67, 'LOW', 4, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (5, 401, 19, 20, 95.00, 'FULL', 3, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (6, 402, 50, 120, 41.67, 'LOW', 18, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (7, 403, 0, 36, 0.00, 'EMPTY', 2, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (8, 404, 34, 50, 68.00, 'MEDIUM', 6, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (9, 405, 5, 40, 12.50, 'EMPTY', 4, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (10, 406, 12, 12, 100.00, 'FULL', 4, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (11, 501, 0, 90, 0.00, 'EMPTY', 0, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (12, 502, 0, 70, 0.00, 'EMPTY', 1, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (13, 503, 73, 80, 91.25, 'FULL', 7, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (14, 601, 3, 25, 12.00, 'LOW', 3, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (15, 602, 28, 30, 93.33, 'FULL', 5, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW()),
    (16, 603, 0, 100, 0.00, 'EMPTY', 2, UNIX_TIMESTAMP(NOW()) * 1000, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    current_occupancy = VALUES(current_occupancy),
    max_capacity = VALUES(max_capacity),
    occupancy_percentage = VALUES(occupancy_percentage),
    crowd_level = VALUES(crowd_level),
    sensor_count = VALUES(sensor_count),
    last_updated_timestamp = VALUES(last_updated_timestamp),
    update_date = NOW();

INSERT INTO occupancy_history (
    id, space_id, occupancy_count, occupancy_percentage, crowd_level, max_capacity, note, create_date, update_date
) VALUES
    (1, 101, 154, 70.00, 'HIGH', 220, '08:00 초기 유입', DATE_SUB(NOW(), INTERVAL 9 HOUR), DATE_SUB(NOW(), INTERVAL 9 HOUR)),
    (2, 101, 184, 83.64, 'HIGH', 220, '11:00 방문객 대기 증가', DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR)),
    (3, 101, 202, 91.82, 'FULL', 220, '14:00 피크 로비 밀집', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR)),
    (4, 102, 88, 48.89, 'MEDIUM', 180, '아침 준비 시간', DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 8 HOUR)),
    (5, 102, 126, 70.00, 'HIGH', 180, '점심 유입 증가', DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),
    (6, 102, 158, 87.78, 'FULL', 180, '점심 피크', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),
    (7, 401, 12, 60.00, 'MEDIUM', 20, '회의 전 입실', DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR)),
    (8, 401, 19, 95.00, 'FULL', 20, '회의 과밀 감지', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR)),
    (9, 402, 28, 23.33, 'LOW', 120, '오전 업무 시작', DATE_SUB(NOW(), INTERVAL 7 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR)),
    (10, 402, 50, 41.67, 'LOW', 120, '현재 활성 인원', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR)),
    (11, 403, 0, 0.00, 'EMPTY', 36, '점검 중 오프라인', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),
    (12, 406, 12, 100.00, 'FULL', 12, '적재 한계 초과', DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
    (13, 501, 0, 0.00, 'EMPTY', 90, '개장 전 워크스페이스 준비', DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR)),
    (14, 503, 35, 43.75, 'MEDIUM', 80, '행사 준비 입장 시작', DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR)),
    (15, 503, 73, 91.25, 'FULL', 80, '루프탑 행사 피크', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR)),
    (16, 601, 8, 32.00, 'LOW', 25, '반입 작업 진행', DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),
    (17, 601, 3, 12.00, 'LOW', 25, '유지보수로 일부 통제', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR)),
    (18, 602, 14, 46.67, 'MEDIUM', 30, '오전 교육 세션', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR)),
    (19, 602, 28, 93.33, 'FULL', 30, '오후 온보딩 피크', DATE_SUB(NOW(), INTERVAL 45 MINUTE), DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
    (20, 603, 0, 0.00, 'EMPTY', 100, '폐쇄 구역으로 출입 없음', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR))
ON DUPLICATE KEY UPDATE
    occupancy_count = VALUES(occupancy_count),
    occupancy_percentage = VALUES(occupancy_percentage),
    crowd_level = VALUES(crowd_level),
    max_capacity = VALUES(max_capacity),
    note = VALUES(note),
    update_date = NOW();

-- ============================================================
-- Sensor / gateway registry (admin-owned metadata)
-- ============================================================
INSERT INTO gateway_registry (
    id, gateway_id, space_id, gateway_name, gateway_role, region_code, location_label, ip_address,
    sensor_capacity, current_sensor_load, latency_ms, packet_loss_percent, last_heartbeat_at,
    status, firmware_version, description, linked_bridge, create_date, update_date
) VALUES
    (1, 'GW-ALPHA-01', 401, 'Hub-Alpha-01', 'HUB', 'EAST', 'Meeting Room Alpha · Core', '192.168.1.10', 150, 18, 12, 0.1, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 'ONLINE', '4.2.0.1', 'Meeting room alpha primary hub', 'mqtt://hub-alpha-01', NOW(), NOW()),
    (2, 'GW-BRAVO-07', 402, 'Hub-Bravo-07', 'HUB', 'CENTRAL', 'Main Workspace · Core', '10.0.4.22', 150, 32, 45, 0.9, DATE_SUB(NOW(), INTERVAL 15 MINUTE), 'MAINTENANCE', '4.1.8', 'Workspace gateway under maintenance window', 'mqtt://hub-bravo-07', NOW(), NOW()),
    (3, 'GW-BETA-05', 101, 'Hub-Beta-05', 'HUB', 'NORTH', 'North Wing Lobby · Entrance', '10.0.1.5', 150, 142, 45, 1.2, DATE_SUB(NOW(), INTERVAL 8 MINUTE), 'WARNING', '4.2.0.1', 'North lobby aggregation hub', 'mqtt://hub-beta-05', NOW(), NOW()),
    (4, 'GW-GAMMA-09', 201, 'Edge-Gamma-09', 'EDGE', 'SOUTH', 'Conference Center · Stage Side', '172.16.0.45', 80, 20, 8, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 'ONLINE', '4.2.0.0', 'Conference center edge gateway', 'mqtt://edge-gamma-09', NOW(), NOW()),
    (5, 'GW-DELTA-02', 403, 'Hub-Delta-02', 'HUB', 'WEST', 'Innovation Lab · Bench Cluster', '192.168.1.15', 150, 0, NULL, 100.0, DATE_SUB(NOW(), INTERVAL 15 MINUTE), 'OFFLINE', '4.0.9', 'Innovation lab offline hub', 'mqtt://hub-delta-02', NOW(), NOW()),
    (6, 'GW-EPSILON-03', 404, 'Hub-Epsilon-03', 'HUB', 'CENTRAL', 'Coffee Lounge · Service Bar', '192.168.2.112', 150, 45, 18, 0.2, DATE_SUB(NOW(), INTERVAL 3 MINUTE), 'ONLINE', '4.2.0.1', 'Coffee lounge primary hub', 'mqtt://hub-epsilon-03', NOW(), NOW()),
    (7, 'GW-X892-01', 405, 'Edge-X892-01', 'EDGE', 'CENTRAL', 'Quiet Pods · Focus Cluster', '172.16.2.92', 64, 24, 10, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 'ONLINE', '4.2.1', 'Quiet pods edge gateway', 'mqtt://edge-x892-01', NOW(), NOW()),
    (8, 'GW-M110-01', 406, 'Hub-M110-01', 'HUB', 'SOUTH', 'Storage B · Loading Rack', '10.0.5.10', 40, 4, 22, 0.5, DATE_SUB(NOW(), INTERVAL 1 HOUR), 'ONLINE', '4.2.1', 'Storage support hub', 'mqtt://hub-m110-01', NOW(), NOW()),
    (9, 'GW-LOUNGE-04', 301, 'Edge-Lounge-04', 'EDGE', 'WEST', 'Staff Lounge · Coffee Bar', '172.16.4.30', 48, 8, 11, 0.0, DATE_SUB(NOW(), INTERVAL 5 MINUTE), 'ONLINE', '4.1.9', 'Staff lounge edge gateway', 'mqtt://edge-lounge-04', NOW(), NOW()),
    (10, 'GW-BRAVO-08', 402, 'Edge-Bravo-08', 'EDGE', 'CENTRAL', 'Main Workspace · East Wing', '10.0.4.23', 96, 48, 14, 0.1, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 'ONLINE', '4.2.1', 'Secondary workspace edge gateway', 'mqtt://edge-bravo-08', NOW(), NOW()),
    (11, 'GW-STAGING-01', NULL, 'Stage-Gateway-01', 'EDGE', NULL, 'Inventory Staging Rack', '192.168.99.10', 32, 0, NULL, NULL, NULL, 'REGISTERED', '4.3.0-rc1', 'Unassigned staging gateway for provisioning', 'mqtt://stage-gateway-01', NOW(), NOW()),
    (12, 'GW-NORTH-EDGE-02', 101, 'North-Edge-02', 'EDGE', 'NORTH', 'North Wing Lobby · Kiosk Edge', '10.0.1.6', 64, 28, 9, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 'ONLINE', '4.2.1', 'Lobby kiosk edge gateway', 'mqtt://north-edge-02', NOW(), NOW()),
    (13, 'GW-CONF-BACKUP-02', 201, 'Conf-Backup-02', 'EDGE', 'SOUTH', 'Conference Center · Backstage', '172.16.0.46', 96, 74, 27, 0.4, DATE_SUB(NOW(), INTERVAL 7 MINUTE), 'WARNING', '4.1.7', 'Conference backup gateway under high load', 'mqtt://conf-backup-02', NOW(), NOW()),
    (14, 'GW-SOUTH-ANNEX-01', 501, 'South-Annex-01', 'HUB', 'SOUTH', 'South Annex Workspace · Core', '10.1.1.21', 64, 0, 5, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 'ONLINE', '4.2.2', '준비 완료된 무부하 게이트웨이', 'mqtt://south-annex-01', NOW(), NOW()),
    (15, 'GW-SOUTH-ANNEX-02', 501, 'South-Annex-02', 'EDGE', 'SOUTH', 'South Annex Workspace · East Wing', '10.1.1.22', 64, 63, 38, 1.5, DATE_SUB(NOW(), INTERVAL 6 MINUTE), 'WARNING', '4.2.1', '과부하 직전 상태의 에지 게이트웨이', 'mqtt://south-annex-02', NOW(), NOW()),
    (16, 'GW-DRAFT-01', 502, 'Draft-Deploy-01', 'EDGE', 'SOUTH', 'Expansion Draft Zone · Staging', '10.1.5.10', 32, 1, NULL, NULL, NULL, 'REGISTERED', '4.3.0-rc2', '오픈 전 설치 준비용 게이트웨이', 'mqtt://draft-deploy-01', NOW(), NOW()),
    (17, 'GW-ROOF-01', 503, 'Roof-Event-01', 'HUB', 'EAST', 'Rooftop Event Deck · Main Rig', '10.1.9.3', 96, 48, 19, 0.3, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 'ONLINE', '4.2.2', '루프탑 행사 메인 허브', 'mqtt://roof-event-01', NOW(), NOW()),
    (18, 'GW-BASEMENT-01', 601, 'Basement-Support-01', 'HUB', 'WEST', 'Basement Support Hub · Dock', '10.2.1.8', 48, 6, NULL, 24.5, DATE_SUB(NOW(), INTERVAL 4 HOUR), 'OFFLINE', '4.0.8', '지하 반입구 허브 장애 상태', 'mqtt://basement-support-01', NOW(), NOW()),
    (19, 'GW-TRAIN-01', 602, 'Training-Core-01', 'EDGE', 'CENTRAL', 'Training Room Delta · Instructor Side', '10.2.2.11', 24, 24, 13, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 'ONLINE', '4.2.1', '정원 가득 찬 교육장 게이트웨이', 'mqtt://training-core-01', NOW(), NOW()),
    (20, 'GW-ARCHIVE-01', 603, 'Archive-Core-01', 'EDGE', 'NORTH', 'Archive Vault · Security Door', '10.3.9.1', 32, 2, 31, 3.0, DATE_SUB(NOW(), INTERVAL 40 MINUTE), 'MAINTENANCE', '4.1.5', '폐쇄 공간 유지보수 게이트웨이', 'mqtt://archive-core-01', NOW(), NOW()),
    (21, 'GW-INVENTORY-02', NULL, 'Inventory-Gateway-02', 'EDGE', NULL, 'Provisioning Cart #2', '192.168.99.11', 16, 0, NULL, NULL, NULL, 'REGISTERED', '4.3.0-rc2', '출고 대기 중인 추가 게이트웨이', 'mqtt://inventory-gateway-02', NOW(), NOW()),
    (22, 'GW-DR-FAILOVER-01', 201, 'Disaster-Recovery-01', 'EDGE', 'SOUTH', 'Conference Center · Failover Rack', '172.16.0.99', 32, 4, NULL, 12.5, DATE_SUB(NOW(), INTERVAL 8 HOUR), 'OFFLINE', '4.0.7', '장애 복구용 예비 게이트웨이', 'mqtt://dr-failover-01', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    space_id = VALUES(space_id),
    gateway_name = VALUES(gateway_name),
    gateway_role = VALUES(gateway_role),
    region_code = VALUES(region_code),
    location_label = VALUES(location_label),
    ip_address = VALUES(ip_address),
    sensor_capacity = VALUES(sensor_capacity),
    current_sensor_load = VALUES(current_sensor_load),
    latency_ms = VALUES(latency_ms),
    packet_loss_percent = VALUES(packet_loss_percent),
    last_heartbeat_at = VALUES(last_heartbeat_at),
    status = VALUES(status),
    firmware_version = VALUES(firmware_version),
    description = VALUES(description),
    linked_bridge = VALUES(linked_bridge),
    update_date = NOW();

INSERT INTO sensor_registry (
    id, sensor_id, mac_address, model, firmware_version, type, protocol, status,
    gateway_id, position_code, battery_percent, occupancy_threshold_cm, calibration_offset_cm,
    last_heartbeat_at, last_sequence_no, metadata_json, create_date, update_date
) VALUES
    (1, 'SN-PIR-2041', 'AA:BB:CC:20:40:41', 'PIR-Pro-X', '2.4.1', 'PIR', 'MQTT', 'ACTIVE', 'GW-ALPHA-01', 'MRA-SEAT-01', 88.0, 120.0, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 2041, '{"zone":"meeting-room-alpha","displayStatus":"Operational"}', NOW(), NOW()),
    (2, 'SN-ULT-9912', 'AA:BB:CC:20:99:12', 'UltraSense-9', '2.4.1', 'ULTRASONIC', 'MQTT', 'LOW_BATTERY', 'GW-ALPHA-01', 'MRA-ENTRY-02', 24.0, 118.0, 0.0, DATE_SUB(NOW(), INTERVAL 14 MINUTE), 9912, '{"zone":"meeting-room-alpha","displayStatus":"Low Battery"}', NOW(), NOW()),
    (3, 'SN-PIR-5502', 'AA:BB:CC:20:55:02', 'PIR-Pro-X', '2.4.1', 'PIR', 'MQTT', 'ACTIVE', 'GW-ALPHA-01', 'MRA-SEAT-03', 92.0, 120.0, 0.0, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 5502, '{"zone":"meeting-room-alpha","displayStatus":"Operational"}', NOW(), NOW()),
    (4, 'SN-ULT-1102', 'AA:BB:CC:21:11:02', 'UltraSense-9', '2.3.8', 'ULTRASONIC', 'MQTT', 'OFFLINE', 'GW-BRAVO-07', 'MWS-ENTRY-01', 5.0, 122.0, 1.0, DATE_SUB(NOW(), INTERVAL 1 HOUR), 1102, '{"zone":"main-workspace","displayStatus":"Offline"}', NOW(), NOW()),
    (5, 'SN-PIR-6671', 'AA:BB:CC:21:66:71', 'PIR-Lite', '2.3.8', 'PIR', 'MQTT', 'CALIBRATING', 'GW-BRAVO-07', 'MWS-ROW-B', 67.0, 122.0, 1.5, DATE_SUB(NOW(), INTERVAL 5 MINUTE), 6671, '{"zone":"main-workspace","displayStatus":"Calibrating"}', NOW(), NOW()),
    (6, 'SN-LOBBY-9201', 'AA:BB:CC:22:92:01', 'WideBeam', '2.4.0', 'PIR', 'MQTT', 'ACTIVE', 'GW-BETA-05', 'NWL-CENTER', 81.0, 125.0, 0.0, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 9201, '{"zone":"north-wing-lobby"}', NOW(), NOW()),
    (7, 'SN-DINING-8801', 'AA:BB:CC:23:88:01', 'WideBeam', '2.4.0', 'PIR', 'MQTT', 'ACTIVE', 'GW-BETA-05', 'DHB-LINE', 76.0, 125.0, 0.0, DATE_SUB(NOW(), INTERVAL 3 MINUTE), 8801, '{"zone":"dining-hall-b"}', NOW(), NOW()),
    (8, 'SN-CONF-6401', 'AA:BB:CC:24:64:01', 'UltraSense-9', '2.4.0', 'ULTRASONIC', 'MQTT', 'ACTIVE', 'GW-GAMMA-09', 'CC-STAGE', 93.0, 119.0, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 6401, '{"zone":"conference-center"}', NOW(), NOW()),
    (9, 'SN-LOUNGE-4201', 'AA:BB:CC:25:42:01', 'PIR-Lite', '2.4.0', 'PIR', 'MQTT', 'ACTIVE', 'GW-LOUNGE-04', 'SL-SOFA', 71.0, 117.0, 0.0, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 4201, '{"zone":"staff-lounge"}', NOW(), NOW()),
    (10, 'SN-COFFEE-6801', 'AA:BB:CC:26:68:01', 'PIR-Lite', '2.4.2', 'PIR', 'MQTT', 'ACTIVE', 'GW-EPSILON-03', 'CL-COUNTER', 83.0, 118.0, 0.0, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 6801, '{"zone":"coffee-lounge"}', NOW(), NOW()),
    (11, 'SN-QPOD-1201', 'AA:BB:CC:27:12:01', 'QuietSense', '2.4.2', 'PIR', 'MQTT', 'ACTIVE', 'GW-X892-01', 'QP-01', 95.0, 116.0, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 1201, '{"zone":"quiet-pods"}', NOW(), NOW()),
    (12, 'SN-STOR-0005', 'AA:BB:CC:28:00:05', 'UltraSense-9', '2.3.7', 'ULTRASONIC', 'MQTT', 'ACTIVE', 'GW-M110-01', 'STB-RACK-01', 58.0, 123.0, 0.0, DATE_SUB(NOW(), INTERVAL 6 MINUTE), 5, '{"zone":"storage-b"}', NOW(), NOW()),
    (13, 'SN-LAB-3051', 'AA:BB:CC:29:30:51', 'LabMesh', '2.2.0', 'PIR', 'MQTT', 'MAINTENANCE', 'GW-DELTA-02', 'LAB-BENCH-01', 44.0, 120.0, 0.0, DATE_SUB(NOW(), INTERVAL 10 HOUR), 3051, '{"zone":"innovation-lab"}', NOW(), NOW()),
    (14, 'SN-STAGE-UNASSIGNED-01', 'AA:BB:CC:30:70:01', 'ESP32-C6', '2.2.0', 'OCCUPANCY_DETECTION', 'MQTT', 'REGISTERED', NULL, NULL, 100.0, 120.0, 0.0, NULL, NULL, '{"zone":"staging","assigned":false}', NOW(), NOW()),
    (15, 'SN-PIR-8808', 'AA:BB:CC:31:88:08', 'PIR-Pro-X', '2.4.2', 'PIR', 'MQTT', 'ACTIVE', 'GW-BRAVO-08', 'MWS-EAST-08', 86.0, 121.0, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 8808, '{"zone":"main-workspace-east","displayStatus":"Operational"}', NOW(), NOW()),
    (16, 'SN-THERM-8809', 'AA:BB:CC:31:88:09', 'ThermalGrid', '1.9.1', 'THERMAL', 'MQTT', 'ACTIVE', 'GW-BRAVO-08', 'MWS-EAST-09', 63.0, 0.1, 0.0, DATE_SUB(NOW(), INTERVAL 3 MINUTE), 8809, '{"zone":"main-workspace-east","displayStatus":"Thermal Tracking"}', NOW(), NOW()),
    (17, 'SN-PIR-8810', 'AA:BB:CC:31:88:10', 'PIR-Pro-X', '2.4.2', 'PIR', 'MQTT', 'DECOMMISSIONED', 'GW-BRAVO-08', 'MWS-EAST-10', 0.0, 120.0, 0.0, DATE_SUB(NOW(), INTERVAL 30 DAY), 8810, '{"zone":"main-workspace-east","displayStatus":"Decommissioned"}', NOW(), NOW()),
    (18, 'SN-CO2-1012', 'AA:BB:CC:32:10:12', 'AirSense-CO2', '3.0.0', 'CO2', 'MQTT', 'ACTIVE', 'GW-NORTH-EDGE-02', 'NWL-KIOSK-12', 78.0, 0.1, 0.0, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 1012, '{"zone":"north-wing-lobby","displayStatus":"Air Quality"}', NOW(), NOW()),
    (19, 'SN-PIR-1013', 'AA:BB:CC:32:10:13', 'PIR-Lite', '2.4.0', 'PIR', 'MQTT', 'LOW_BATTERY', 'GW-NORTH-EDGE-02', 'NWL-KIOSK-13', 11.0, 124.0, 0.0, DATE_SUB(NOW(), INTERVAL 12 MINUTE), 1013, '{"zone":"north-wing-lobby","displayStatus":"Low Battery"}', NOW(), NOW()),
    (20, 'SN-THERM-2011', 'AA:BB:CC:33:20:11', 'ThermalGrid', '1.9.0', 'THERMAL', 'MQTT', 'ACTIVE', 'GW-CONF-BACKUP-02', 'CC-BACK-11', 69.0, 0.1, 0.0, DATE_SUB(NOW(), INTERVAL 4 MINUTE), 2011, '{"zone":"conference-center","displayStatus":"Operational"}', NOW(), NOW()),
    (21, 'SN-PIR-2012', 'AA:BB:CC:33:20:12', 'PIR-Pro-X', '2.4.0', 'PIR', 'MQTT', 'OFFLINE', 'GW-CONF-BACKUP-02', 'CC-BACK-12', 0.0, 119.0, 0.0, DATE_SUB(NOW(), INTERVAL 2 HOUR), 2012, '{"zone":"conference-center","displayStatus":"Offline"}', NOW(), NOW()),
    (22, 'SN-LAB-3052', 'AA:BB:CC:34:30:52', 'LabMesh', '2.2.0', 'PIR', 'MQTT', 'OFFLINE', 'GW-DELTA-02', 'LAB-BENCH-02', 0.0, 120.0, 0.0, DATE_SUB(NOW(), INTERVAL 6 HOUR), 3052, '{"zone":"innovation-lab","displayStatus":"Offline"}', NOW(), NOW()),
    (23, 'SN-STAGE-GW-01', 'AA:BB:CC:35:70:02', 'ESP32-C6', '2.3.0-rc1', 'PIR', 'MQTT', 'REGISTERED', 'GW-STAGING-01', 'STAGE-RACK-01', 100.0, 118.0, 0.0, NULL, NULL, '{"zone":"staging","displayStatus":"Provisioning"}', NOW(), NOW()),
    (24, 'SN-STAGE-GW-02', 'AA:BB:CC:35:70:03', 'ESP32-C6', '2.3.0-rc1', 'ULTRASONIC', 'MQTT', 'REGISTERED', 'GW-STAGING-01', 'STAGE-RACK-02', 100.0, 118.0, 0.0, NULL, NULL, '{"zone":"staging","displayStatus":"Provisioning"}', NOW(), NOW()),
    (25, 'SN-SOUTH-0001', 'AA:BB:CC:36:00:01', 'PIR-Pro-X', '2.4.2', 'PIR', 'MQTT', 'ACTIVE', 'GW-SOUTH-ANNEX-02', 'SAW-EAST-01', 97.0, 120.0, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 1, '{"zone":"south-annex-workspace","displayStatus":"Operational"}', NOW(), NOW()),
    (26, 'SN-SOUTH-0002', 'AA:BB:CC:36:00:02', 'AirSense-CO2', '3.0.1', 'CO2', 'MQTT', 'ACTIVE', 'GW-SOUTH-ANNEX-02', 'SAW-EAST-02', 82.0, 0.1, 0.0, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 2, '{"zone":"south-annex-workspace","displayStatus":"Air Quality"}', NOW(), NOW()),
    (27, 'SN-SOUTH-0003', 'AA:BB:CC:36:00:03', 'PIR-Lite', '2.4.1', 'PIR', 'MQTT', 'INACTIVE', 'GW-SOUTH-ANNEX-02', 'SAW-EAST-03', 60.0, 121.0, 0.0, DATE_SUB(NOW(), INTERVAL 3 DAY), 3, '{"zone":"south-annex-workspace","displayStatus":"Inactive"}', NOW(), NOW()),
    (28, 'SN-SOUTH-0004', 'AA:BB:CC:36:00:04', 'ThermalGrid', '1.9.1', 'THERMAL', 'MQTT', 'LOW_BATTERY', 'GW-SOUTH-ANNEX-02', 'SAW-EAST-04', 8.0, 0.1, 0.0, DATE_SUB(NOW(), INTERVAL 11 MINUTE), 4, '{"zone":"south-annex-workspace","displayStatus":"Low Battery"}', NOW(), NOW()),
    (29, 'SN-DRAFT-0101', 'AA:BB:CC:37:01:01', 'ESP32-C6', '2.3.0-rc2', 'PIR', 'MQTT', 'REGISTERED', 'GW-DRAFT-01', 'DRAFT-01', 100.0, 119.0, 0.0, NULL, NULL, '{"zone":"expansion-draft-zone","displayStatus":"Provisioning"}', NOW(), NOW()),
    (30, 'SN-ROOF-0101', 'AA:BB:CC:38:01:01', 'WeatherSense', '1.4.0', 'PIR', 'MQTT', 'ACTIVE', 'GW-ROOF-01', 'ROOF-ENTRY-01', 84.0, 120.0, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 101, '{"zone":"rooftop-event-deck","displayStatus":"Operational"}', NOW(), NOW()),
    (31, 'SN-ROOF-0102', 'AA:BB:CC:38:01:02', 'WeatherSense', '1.4.0', 'ULTRASONIC', 'MQTT', 'ACTIVE', 'GW-ROOF-01', 'ROOF-STAGE-02', 76.0, 123.0, -0.5, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 102, '{"zone":"rooftop-event-deck","displayStatus":"Operational"}', NOW(), NOW()),
    (32, 'SN-ROOF-0103', 'AA:BB:CC:38:01:03', 'WeatherSense', '1.4.0', 'PIR', 'MQTT', 'MAINTENANCE', 'GW-ROOF-01', 'ROOF-BAR-03', NULL, 121.0, 0.0, DATE_SUB(NOW(), INTERVAL 1 DAY), 103, '{"zone":"rooftop-event-deck","displayStatus":"Maintenance"}', NOW(), NOW()),
    (33, 'SN-ROOF-0104', 'AA:BB:CC:38:01:04', 'WeatherSense', '1.4.0', 'CO2', 'MQTT', 'LOW_BATTERY', 'GW-ROOF-01', 'ROOF-BAR-04', 14.0, 0.1, 0.0, DATE_SUB(NOW(), INTERVAL 12 MINUTE), 104, '{"zone":"rooftop-event-deck","displayStatus":"Low Battery"}', NOW(), NOW()),
    (34, 'SN-BASE-0101', 'AA:BB:CC:39:01:01', 'DockSense', '2.0.0', 'PIR', 'MQTT', 'OFFLINE', 'GW-BASEMENT-01', 'BSH-DOCK-01', 0.0, 122.0, 0.0, DATE_SUB(NOW(), INTERVAL 4 HOUR), 201, '{"zone":"basement-support-hub","displayStatus":"Offline"}', NOW(), NOW()),
    (35, 'SN-BASE-0102', 'AA:BB:CC:39:01:02', 'DockSense', '2.0.0', 'ULTRASONIC', 'MQTT', 'MAINTENANCE', 'GW-BASEMENT-01', 'BSH-DOCK-02', 41.0, 122.0, 0.0, DATE_SUB(NOW(), INTERVAL 8 HOUR), 202, '{"zone":"basement-support-hub","displayStatus":"Maintenance"}', NOW(), NOW()),
    (36, 'SN-TRAIN-0101', 'AA:BB:CC:40:01:01', 'ClassSense', '2.5.0', 'PIR', 'MQTT', 'ACTIVE', 'GW-TRAIN-01', 'TRD-ROW-01', 88.0, 118.0, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 301, '{"zone":"training-room-delta","displayStatus":"Operational"}', NOW(), NOW()),
    (37, 'SN-TRAIN-0102', 'AA:BB:CC:40:01:02', 'ClassSense', '2.5.0', 'PIR', 'MQTT', 'ACTIVE', 'GW-TRAIN-01', 'TRD-ROW-02', 91.0, 118.0, 0.0, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 302, '{"zone":"training-room-delta","displayStatus":"Operational"}', NOW(), NOW()),
    (38, 'SN-TRAIN-0103', 'AA:BB:CC:40:01:03', 'ClassSense', '2.5.0', 'ULTRASONIC', 'MQTT', 'ACTIVE', 'GW-TRAIN-01', 'TRD-ROW-03', 73.0, 118.0, 0.0, DATE_SUB(NOW(), INTERVAL 2 MINUTE), 303, '{"zone":"training-room-delta","displayStatus":"Operational"}', NOW(), NOW()),
    (39, 'SN-ARCH-0101', 'AA:BB:CC:41:01:01', 'VaultSense', '1.1.0', 'PIR', 'MQTT', 'DECOMMISSIONED', 'GW-ARCHIVE-01', 'ARC-01', 0.0, 125.0, 0.0, DATE_SUB(NOW(), INTERVAL 90 DAY), 401, '{"zone":"archive-vault","displayStatus":"Decommissioned"}', NOW(), NOW()),
    (40, 'SN-ARCH-0102', 'AA:BB:CC:41:01:02', 'VaultSense', '1.1.0', 'PIR', 'MQTT', 'MAINTENANCE', 'GW-ARCHIVE-01', 'ARC-02', NULL, 125.0, 0.0, DATE_SUB(NOW(), INTERVAL 20 DAY), 402, '{"zone":"archive-vault","displayStatus":"Maintenance"}', NOW(), NOW()),
    (41, 'SN-INV-0201', 'AA:BB:CC:42:02:01', 'ESP32-C6', '2.3.0-rc2', 'PIR', 'MQTT', 'REGISTERED', 'GW-INVENTORY-02', 'INV-CART-01', 100.0, 120.0, 0.0, NULL, NULL, '{"zone":"inventory","displayStatus":"Provisioning"}', NOW(), NOW()),
    (42, 'SN-UNASSIGNED-02', 'AA:BB:CC:43:02:01', 'ESP32-C6', '2.3.0-rc2', 'ULTRASONIC', 'MQTT', 'REGISTERED', NULL, NULL, NULL, 118.0, 0.0, NULL, NULL, '{"zone":"inventory","assigned":false,"displayStatus":"Awaiting Gateway"}', NOW(), NOW()),
    (43, 'SN-DR-FAIL-01', 'AA:BB:CC:44:02:01', 'PIR-Lite', '2.2.9', 'PIR', 'MQTT', 'OFFLINE', 'GW-DR-FAILOVER-01', 'CC-DR-01', 0.0, 119.0, 0.0, DATE_SUB(NOW(), INTERVAL 8 HOUR), 501, '{"zone":"conference-center","displayStatus":"Failover Offline"}', NOW(), NOW()),
    (44, 'SN-DR-FAIL-02', 'AA:BB:CC:44:02:02', 'PIR-Lite', '2.2.9', 'PIR', 'MQTT', 'ACTIVE', 'GW-DR-FAILOVER-01', 'CC-DR-02', 57.0, 119.0, 0.0, DATE_SUB(NOW(), INTERVAL 9 HOUR), 502, '{"zone":"conference-center","displayStatus":"Recovered Partially"}', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    mac_address = VALUES(mac_address),
    model = VALUES(model),
    firmware_version = VALUES(firmware_version),
    type = VALUES(type),
    protocol = VALUES(protocol),
    status = VALUES(status),
    gateway_id = VALUES(gateway_id),
    position_code = VALUES(position_code),
    battery_percent = VALUES(battery_percent),
    occupancy_threshold_cm = VALUES(occupancy_threshold_cm),
    calibration_offset_cm = VALUES(calibration_offset_cm),
    last_heartbeat_at = VALUES(last_heartbeat_at),
    last_sequence_no = VALUES(last_sequence_no),
    metadata_json = VALUES(metadata_json),
    update_date = NOW();

-- ============================================================
-- Admin preferences / notifications
-- ============================================================
INSERT INTO user_preference (
    id, profile_id, preference_key, preference_value, description, create_date, update_date
) VALUES
    (1, 1, 'admin_overcapacity_limit', '150', 'Admin overcapacity limit', NOW(), NOW()),
    (2, 1, 'admin_warning_buffer_percent', '85', 'Admin warning buffer percent', NOW(), NOW()),
    (3, 1, 'admin_sensor_raw_data_retention', '90 Days', 'Admin sensor raw data retention', NOW(), NOW()),
    (4, 1, 'admin_system_error_retention', '30 Days', 'Admin system error retention', NOW(), NOW()),
    (5, 1, 'admin_alert_history_retention', '1 Year', 'Admin alert history retention', NOW(), NOW()),
    (6, 2, 'admin_overcapacity_limit', '160', 'Admin overcapacity limit', NOW(), NOW()),
    (7, 2, 'admin_warning_buffer_percent', '80', 'Admin warning buffer percent', NOW(), NOW()),
    (8, 4, 'admin_overcapacity_limit', '135', 'Admin overcapacity limit', NOW(), NOW()),
    (9, 4, 'admin_warning_buffer_percent', '78', 'Admin warning buffer percent', NOW(), NOW()),
    (10, 4, 'admin_sensor_raw_data_retention', '180 Days', 'Admin sensor raw data retention', NOW(), NOW()),
    (11, 4, 'admin_system_error_retention', '90 Days', 'Admin system error retention', NOW(), NOW()),
    (12, 4, 'admin_alert_history_retention', '2 Years', 'Admin alert history retention', NOW(), NOW()),
    (13, 4, 'theme', 'dark', 'Admin console theme preference', NOW(), NOW()),
    (14, 4, 'language', 'ko', 'Admin console language preference', NOW(), NOW()),
    (15, 5, 'admin_overcapacity_limit', '110', 'Admin overcapacity limit', NOW(), NOW()),
    (16, 5, 'admin_warning_buffer_percent', '70', 'Admin warning buffer percent', NOW(), NOW()),
    (17, 5, 'admin_sensor_raw_data_retention', '365 Days', 'Admin sensor raw data retention', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    preference_value = VALUES(preference_value),
    description = VALUES(description),
    update_date = NOW();

INSERT INTO notification_preference (
    id, profile_id, all_notifications_enabled, occupancy_notifications_enabled, battery_notifications_enabled,
    review_notifications_enabled, promotion_notifications_enabled, email_notifications_enabled,
    push_notifications_enabled, sms_notifications_enabled, create_date, update_date
) VALUES
    (1, 1, 1, 1, 1, 1, 0, 0, 1, 0, NOW(), NOW()),
    (2, 2, 1, 1, 1, 1, 1, 0, 1, 0, NOW(), NOW()),
    (3, 3, 1, 1, 0, 1, 1, 1, 1, 0, NOW(), NOW()),
    (4, 4, 1, 1, 1, 0, 0, 1, 1, 1, NOW(), NOW()),
    (5, 5, 1, 0, 1, 0, 0, 1, 1, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    all_notifications_enabled = VALUES(all_notifications_enabled),
    occupancy_notifications_enabled = VALUES(occupancy_notifications_enabled),
    battery_notifications_enabled = VALUES(battery_notifications_enabled),
    review_notifications_enabled = VALUES(review_notifications_enabled),
    promotion_notifications_enabled = VALUES(promotion_notifications_enabled),
    email_notifications_enabled = VALUES(email_notifications_enabled),
    push_notifications_enabled = VALUES(push_notifications_enabled),
    sms_notifications_enabled = VALUES(sms_notifications_enabled),
    update_date = NOW();

INSERT INTO notification (
    id, profile_id, title, message, type, read_yn, related_entity_id, related_entity_type, create_date, update_date
) VALUES
    (1, 1, 'Admin Console', '관리자 워크스페이스가 갱신되었습니다.', 'SYSTEM', 0, 'workspace', 'ADMIN', NOW(), NOW()),
    (2, 1, 'Battery Watch', 'North Wing Lobby와 Meeting Room Alpha에서 저전량 센서가 감지되었습니다.', 'LOW_BATTERY', 0, 'SN-ULT-9912', 'SENSOR', NOW(), NOW()),
    (3, 2, 'Capacity Alert', 'North Wing Lobby가 경고 임계치에 도달했습니다.', 'SPACE_OCCUPANCY', 0, '101', 'SPACE', NOW(), NOW()),
    (4, 4, 'Morning Rush', 'Meeting Room Alpha가 수용인원의 95%에 도달했습니다.', 'SPACE_OCCUPANCY', 0, '401', 'SPACE', NOW(), NOW()),
    (5, 4, 'Gateway Offline', 'Innovation Lab의 GW-DELTA-02 응답이 끊어졌습니다.', 'SYSTEM', 0, 'GW-DELTA-02', 'GATEWAY', NOW(), NOW()),
    (6, 4, 'Low Battery', 'SN-ULT-9912 센서의 배터리가 25% 미만입니다.', 'LOW_BATTERY', 0, 'SN-ULT-9912', 'SENSOR', NOW(), NOW()),
    (7, 4, 'Draft Space', 'Expansion Draft Zone은 아직 비활성/미검증 상태입니다.', 'SYSTEM', 1, '502', 'SPACE', NOW(), NOW()),
    (8, 4, 'Staging Device', '미배치 센서와 등록 대기 게이트웨이가 인벤토리에 남아 있습니다.', 'SYSTEM', 1, 'staging-devices', 'INVENTORY', NOW(), NOW()),
    (9, 2, 'Rooftop Peak', 'Rooftop Event Deck가 행사 피크로 90%를 넘겼습니다.', 'SPACE_OCCUPANCY', 0, '503', 'SPACE', NOW(), NOW()),
    (10, 2, 'High Load Gateway', 'GW-SOUTH-ANNEX-02가 센서 용량 한계에 근접했습니다.', 'SYSTEM', 0, 'GW-SOUTH-ANNEX-02', 'GATEWAY', NOW(), NOW()),
    (11, 3, 'Training Full', 'Training Room Delta가 정원에 근접했습니다.', 'SPACE_OCCUPANCY', 0, '602', 'SPACE', NOW(), NOW()),
    (12, 3, 'Basement Maintenance', 'Basement Support Hub 센서 일부가 유지보수 중입니다.', 'SYSTEM', 0, '601', 'SPACE', NOW(), NOW()),
    (13, 5, 'Archive Closed', 'Archive Vault는 폐쇄 운영 상태입니다.', 'SYSTEM', 1, '603', 'SPACE', NOW(), NOW()),
    (14, 5, 'Decommissioned Sensor', 'SN-ARCH-0101 은(는) 폐기 처리 상태입니다.', 'SYSTEM', 1, 'SN-ARCH-0101', 'SENSOR', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    message = VALUES(message),
    type = VALUES(type),
    read_yn = VALUES(read_yn),
    related_entity_id = VALUES(related_entity_id),
    related_entity_type = VALUES(related_entity_type),
    update_date = NOW();
