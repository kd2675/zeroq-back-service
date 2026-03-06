# zeroq-back-service

ZeroQ 핵심 비즈니스 API입니다. 공간 조회, 혼잡도, 리뷰, 즐겨찾기, 사용자 위치 기록을 제공하며 인증은 `auth-back-server`, 진입은 `cloud-back-server`를 전제로 합니다.

## 역할

- `zeroq-front-service`와 `zeroq-front-admin`이 호출하는 메인 도메인 API
- Gateway가 주입한 `UserContext` 기반 사용자 식별 처리
- Eureka client, OpenFeign, JPA, Cache, Actuator 사용

## 주요 도메인

- `space`: 공간 목록, 상세, 카테고리, 검색, top-rated
- `occupancy`: 공간별 현재 혼잡도, 이력, 평균
- `review`: 공간 리뷰 조회/작성/삭제, 평점 집계
- `favorite`: 사용자 즐겨찾기 조회/등록/삭제
- `userlocation`: 사용자 위치 기록과 방문 카운트

## API 베이스 경로

- `/api/zeroq/v1/spaces`
- `/api/zeroq/v1/occupancy`
- `/api/zeroq/v1/reviews`
- `/api/zeroq/v1/favorites`
- `/api/zeroq/v1/user-locations`

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

## 데이터와 리소스

- 설정: `src/main/resources/application*.yml`
- 로그: `src/main/resources/logback-spring.xml`
- 전체 DDL: `src/main/resources/db/ddl/zeroq_all.sql`
- 데이터 리셋: `src/main/resources/db/ddl/zeroq_data_reset_keep_users.sql`
- 개발 시드: `src/main/resources/db/seed/zeroq_content_seed.sql`

## 참고

- 역할 모델은 `USER`, `MANAGER`, `ADMIN`입니다.
- 테스트 파일은 존재하지만 현재 회귀 범위는 넓지 않고 `contextLoads` 비중이 큽니다.
- 사용자 계정 원천 데이터는 `auth-back-server`가 소유하고, 본 서비스는 `user_key`를 중심으로 연결합니다.
