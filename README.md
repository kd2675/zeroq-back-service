# ZeroQ Back Service

ZeroQ 메인 REST API 서비스입니다. 공간(Spaces), 점유율(Occupancy), 리뷰(Reviews), 즐겨찾기(Favorites), 사용자 위치(User Locations) 도메인을 제공합니다. 사용자 인증/계정은 `auth-back-server`가 담당하며, 본 서비스는 인증된 사용자 식별자를 사용합니다.

## 개요

- **역할**: ZeroQ 핵심 비즈니스 API 제공
- **인증 주체**: `auth-back-server`
- **서비스 디스커버리**: Eureka 기본 활성화

## 서비스 대상/역할 모델

- `USER`: 일반 사용자 (클라이언트: `zeroq-front-service`)
- `MANAGER`: 매장 중간관리자 (클라이언트: `zeroq-front-admin`)
- `ADMIN`: 플랫폼 운영자 (최상위 운영 권한)

역할 정보는 Gateway가 JWT에서 추출한 `X-User-Role` 헤더를 통해 전달하며,
이 서비스는 `UserContext`로 해당 값을 사용합니다.

## 실행 프로필과 포트

| 프로필 | 포트 | 설명 |
|---|---:|---|
| local (기본) | 20180 | 로컬 개발 |
| dev | 20180 | 개발 환경 |
| prod | 10180 | 운영 환경 |
| test | 30180 | 테스트 환경 |

## Base URL

- **게이트웨이 경유만 사용(cloud-back-server)**
  - 로컬 기준: `http://localhost:8080/api/zeroq/v1`
  - 정책: 클라이언트에서 서비스 직행 포트 호출 금지

## 인증 (JWT)

- **Access Token 만료**: 1시간 (3,600,000 ms)
- **Refresh Token 만료**: 14일 (1,209,600,000 ms)

## 서비스 디스커버리 (Eureka)

- 기본 활성화: `spring.cloud.discovery.enabled=true`
- local 기본 주소: `http://localhost:8761/eureka/`
- dev/prod는 프로필별 설정(`application-dev.yml`, `application-prod.yml`) 사용

## 실행

```bash
./gradlew zeroq-back-service:bootRun
```

프로필 지정:
```bash
./gradlew zeroq-back-service:bootRun --args='--spring.profiles.active=local'
./gradlew zeroq-back-service:bootRun --args='--spring.profiles.active=dev'
./gradlew zeroq-back-service:bootRun --args='--spring.profiles.active=prod'
./gradlew zeroq-back-service:bootRun --args='--spring.profiles.active=test'
```

## API 엔드포인트 개요

### Spaces (`/api/zeroq/v1/spaces`)
- `GET /spaces`
- `GET /spaces/{id}`
- `GET /spaces/category/{categoryId}`
- `GET /spaces/search`
- `GET /spaces/top-rated`
- `POST /spaces` (stubbed)
- `PUT /spaces/{id}` (stubbed)
- `DELETE /spaces/{id}`

### Occupancy (`/api/zeroq/v1/occupancy`)
- `GET /occupancy/spaces/{spaceId}`
- `GET /occupancy/spaces/{spaceId}/history`
- `GET /occupancy/spaces/{spaceId}/average`

### Reviews (`/api/zeroq/v1/reviews`)
- `GET /reviews/spaces/{spaceId}`
- `GET /reviews/users/{userKey}`
- `POST /reviews/spaces/{spaceId}` (Gateway `UserContext` 기반)
- `DELETE /reviews/{reviewId}` (Gateway `UserContext` 기반)
- `GET /reviews/spaces/{spaceId}/rating`

### Favorites (`/api/zeroq/v1/favorites`)
- `GET /favorites` (Gateway `UserContext` 기반)
- `POST /favorites/{spaceId}` (Gateway `UserContext` 기반)
- `DELETE /favorites/{spaceId}` (Gateway `UserContext` 기반)

### User Locations (`/api/zeroq/v1/user-locations`)
- `POST /user-locations`
- `GET /user-locations/{id}`
- `GET /user-locations/me`
- `GET /user-locations/me/space/{spaceId}`
- `GET /user-locations/me/after`
- `GET /user-locations/space/{spaceId}/visits/count`

## 구성 파일

- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-test.yml`
- `src/main/resources/application-prod.yml`
- `src/main/resources/db/ddl/zeroq_all.sql`
- `src/main/resources/db/ddl/zeroq_data_reset_keep_users.sql`
- `src/main/resources/db/seed/zeroq_content_seed.sql`

## DB 스키마/시드 (Muse 스타일 디렉토리 정렬)

- DDL 전체 생성: `src/main/resources/db/ddl/zeroq_all.sql`
- 도메인 데이터 리셋: `src/main/resources/db/ddl/zeroq_data_reset_keep_users.sql`
- 개발 시드: `src/main/resources/db/seed/zeroq_content_seed.sql`

적용 순서:
1. `zeroq_all.sql`
2. `zeroq_content_seed.sql`

초기화 후 재시드:
1. `zeroq_data_reset_keep_users.sql`
2. `zeroq_content_seed.sql`

정책:
- 사용자 계정의 원천 데이터는 `auth` DB/서버가 소유
- ZeroQ 도메인은 `user_key`만 저장하여 인증 사용자와 연결
- 모든 호출은 Gateway 경유(`UserContext`)를 전제로 동작

## 내부 의존성

- `web-common-core` (공통 응답 DTO/유틸)
- `auth-common-core` (Gateway UserContext 연동)
