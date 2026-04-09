# zeroq-back-service

ZeroQ 핵심 비즈니스 API입니다. 공간/매장/센서 원장은 `ZEROQ_ADMIN`에서 읽고, 사용자 프로필/즐겨찾기/리뷰/방문 기록은 `ZEROQ_SERVICE`에서 관리합니다. 인증은 `auth-back-server`, 진입은 `cloud-back-server`를 전제로 합니다.

## 역할

- `zeroq-front-service`와 `zeroq-front-admin`이 호출하는 메인 도메인 API
- Gateway가 주입한 `UserContext` 기반 사용자 식별 처리
- `zeroq-back-sensor`를 호출하는 센서 브리지 계층 제공
- Eureka client, OpenFeign, JPA, Cache, Actuator 사용

## 주요 도메인

- `space`: 공간 목록, 상세, 카테고리, 검색, top-rated
- `occupancy`: 공간별 현재 혼잡도, 이력, 평균
- `review`: 공간 리뷰 조회/작성/삭제, 평점 집계
- `favorite`: 사용자 즐겨찾기 조회/등록/삭제
- `userlocation`: 사용자 위치 기록과 방문 카운트
- `space-sensors`: 공간 센서 오버뷰, 센서 등록/설치/상태변경, 명령 생성, 사용자 스냅샷 조회

## API 베이스 경로

- `/api/zeroq/v1/spaces`
- `/api/zeroq/v1/occupancy`
- `/api/zeroq/v1/reviews`
- `/api/zeroq/v1/favorites`
- `/api/zeroq/v1/user-locations`
- `/api/zeroq/v1/space-sensors`

클라이언트는 서비스 포트를 직접 호출하지 않고 Gateway를 통해 `http://localhost:8080/api/zeroq/v1/**`로 접근하는 것이 기준입니다.

## 실행 프로필과 포트

| Profile | Port |
|---|---:|
| `local` | `20180` |
| `dev` | `20180` |
| `prod` | `10180` |
| `test` | `30180` |

## 실행과 검증

```bash
./gradlew :zeroq-back-service:bootRun
./gradlew :zeroq-back-service:bootRun --args='--spring.profiles.active=local'
./gradlew :zeroq-back-service:compileJava
./gradlew :zeroq-back-service:test
```

## 내부 의존성

- `web-common-core`
- `auth-common-core`

## Related Docs

- `AGENTS.md`
- `AGENTS_ZEROQ_CURRENT_FLOW_ANALYSIS_REPORT.md`
- `AGENTS_ZEROQ_DEVELOPMENT_ONLY.md`

## 데이터와 리소스

- 설정: `src/main/resources/application*.yml`
- 로그: `src/main/resources/logback-spring.xml`
- 서비스 DB DDL (`ZEROQ_SERVICE`, 사용자 도메인 전용): `src/main/resources/db/ddl/zeroq_all.sql`
- 서비스 DB 리셋: `src/main/resources/db/ddl/zeroq_data_reset_keep_users.sql`
- 서비스 DB 시드: `src/main/resources/db/seed/zeroq_content_seed.sql`
- 어드민 DB DDL (`ZEROQ_ADMIN`, 공간/혼잡도/센서/게이트웨이 원장): `src/main/resources/db/ddl/zeroq_admin_all.sql`
- 어드민 DB 리셋: `src/main/resources/db/ddl/zeroq_admin_data_reset.sql`
- 어드민 DB 시드: `src/main/resources/db/seed/zeroq_admin_seed.sql`

### 초기화 순서 (로컬 기준)

```bash
# 1) schema 생성
mysql -uroot -p < src/main/resources/db/ddl/zeroq_all.sql
mysql -uroot -p < src/main/resources/db/ddl/zeroq_admin_all.sql

# 2) seed 적재
mysql -uroot -p < src/main/resources/db/seed/zeroq_content_seed.sql
mysql -uroot -p < src/main/resources/db/seed/zeroq_admin_seed.sql
```

### 통합 초기화 (service + admin + sensor)

루트에서 아래 스크립트를 실행하면 `ZEROQ_SERVICE`, `ZEROQ_ADMIN`, `ZEROQ_SENSOR`를 한 번에 초기화할 수 있습니다.

```bash
# 기본값: localhost:3306 / root / 비밀번호 없음
./scripts/init-zeroq-db.sh

# 비밀번호/접속정보 지정
MYSQL_HOST=localhost MYSQL_PORT=3306 MYSQL_USER=root MYSQL_PASSWORD=1234 ./scripts/init-zeroq-db.sh

# 스키마만 재생성(시드 제외)
./scripts/init-zeroq-db.sh --reset-only
```

## 센서 브리지 설정

```bash
database.datasource.sensor.master.url=jdbc:mysql://localhost:3306/ZEROQ_SENSOR
ZEROQ_SENSOR_BRIDGE_CONNECT_TIMEOUT_MS=3000
ZEROQ_SENSOR_BRIDGE_READ_TIMEOUT_MS=7000
```

## 참고

- 역할 모델은 `USER`, `MANAGER`, `ADMIN`입니다.
- 테스트 파일은 존재하지만 현재 회귀 범위는 넓지 않고 `contextLoads` 비중이 큽니다.
- 사용자 계정 원천 데이터는 `auth-back-server`가 소유하고, 본 서비스는 `user_key`를 중심으로 연결합니다.
- `spaceId`/`sensorId`는 사용자 DB와 어드민 DB를 느슨하게 연결하는 비즈니스 키이며, cross-schema FK는 두지 않습니다.
