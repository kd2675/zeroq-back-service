# ZeroQ Back Service

ZeroQ 메인 REST API 서비스입니다. 공간(Spaces), 점유율(Occupancy), 리뷰(Reviews), 즐겨찾기(Favorites), 사용자 위치(User Locations) 도메인을 제공합니다. 사용자 인증/계정은 `auth-back-server`가 담당하며, 본 서비스는 인증된 사용자 식별자를 사용합니다.

## 개요

- **역할**: ZeroQ 핵심 비즈니스 API 제공
- **인증 주체**: `auth-back-server`
- **서비스 디스커버리**: Eureka 기본 활성화

## 실행 프로필과 포트

| 프로필 | 포트 | 설명 |
|---|---:|---|
| local (기본) | 20180 | 로컬 개발 |
| dev | 20180 | 개발 환경 |
| prod | 10180 | 운영 환경 |
| test | 30180 | 테스트 환경 |

## Base URL

- **직접 호출(서비스 직행)**
  - local/dev: `http://localhost:20180/api/v1`
  - prod: `http://localhost:10180/api/v1`
  - test: `http://localhost:30180/api/v1`
- **게이트웨이 경유(cloud-back-server)**
  - 로컬 기준: `http://localhost:8080/api/v1`

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

### Spaces (`/api/v1/spaces`)
- `GET /spaces`
- `GET /spaces/{id}`
- `GET /spaces/category/{categoryId}`
- `GET /spaces/search`
- `GET /spaces/top-rated`
- `POST /spaces` (stubbed)
- `PUT /spaces/{id}` (stubbed)
- `DELETE /spaces/{id}`

### Occupancy (`/api/v1/occupancy`)
- `GET /occupancy/spaces/{spaceId}`
- `GET /occupancy/spaces/{spaceId}/history`
- `GET /occupancy/spaces/{spaceId}/average`

### Reviews (`/api/v1/reviews`)
- `GET /reviews/spaces/{spaceId}`
- `GET /reviews/users/{userId}`
- `POST /reviews/spaces/{spaceId}`
- `DELETE /reviews/{reviewId}`
- `GET /reviews/spaces/{spaceId}/rating`

### Users (Proxy) (`/api/v1/users`)
- `GET /users/{userId}`
- `GET /users/username/{username}`
- `GET /users/email/{email}`
- `GET /users/{userId}/exists`

### Favorites (`/api/v1/favorites`)
- `GET /favorites?userId={id}`
- `POST /favorites/{spaceId}?userId={id}`
- `DELETE /favorites/{spaceId}?userId={id}`

### User Locations (`/api/v1/user-locations`)
- `POST /user-locations`
- `GET /user-locations/{id}`
- `GET /user-locations/user/{userId}`
- `GET /user-locations/user/{userId}/space/{spaceId}`
- `GET /user-locations/user/{userId}/after`
- `GET /user-locations/space/{spaceId}/visits/count`

## 구성 파일

- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-test.yml`
- `src/main/resources/application-prod.yml`

## 내부 의존성

- `web-common-core` (공통 응답 DTO/유틸)
- `auth-common-core` (Auth DTO/Feign 클라이언트)
