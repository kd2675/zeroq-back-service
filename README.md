# ZeroQ Back Service

REST API backend service for ZeroQ - a space occupancy tracking and management platform built with Spring Boot.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Building & Running](#building--running)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Configuration](#configuration)

## Overview

ZeroQ Back Service provides comprehensive REST APIs for:
- **User Management**: Registration, authentication, profile management
- **Space Management**: Create, update, and manage physical spaces/locations
- **Occupancy Tracking**: Real-time occupancy monitoring and historical analysis
- **Reviews & Ratings**: User reviews and space ratings
- **Favorites**: User bookmarking system

The service uses JWT-based authentication, Spring Data JPA for database operations, and follows a clean layered architecture pattern.

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 4.0.1** - Framework
- **Spring Data JPA** - Database ORM
- **Spring Security** - Authentication & Authorization
- **JWT (JJWT 0.12.3)** - Token-based authentication
- **MySQL 8** - Database
- **Gradle 9.2.1** - Build tool
- **Lombok** - Boilerplate reduction

## Project Structure

```
src/main/java/com/zeroq/back/
├── security/                    # JWT & Authentication
│   ├── JwtTokenProvider         # Token generation/validation
│   ├── JwtAuthenticationFilter  # JWT request filter
│   └── CustomUserDetailsService # User details service
├── common/                      # Shared Infrastructure
│   ├── config/                  # Spring configurations
│   ├── exception/               # Global exception handling
│   ├── datasource/              # Database routing
│   ├── jpa/                     # Base entity & DTO classes
│   └── logback/                 # Logging configuration
├── database/pub/                # Data Access Layer
│   ├── entity/                  # JPA Entities (25 total)
│   ├── repository/              # Spring Data Repositories (23 total)
│   └── dto/                     # Data Transfer Objects
└── service/                     # Business Logic Layer
    ├── auth/                    # Authentication service (act/biz/vo)
    ├── user/                    # User management service
    ├── space/                   # Space management service
    ├── occupancy/               # Occupancy tracking service
    └── review/                  # Review management service
```

## Getting Started

### Prerequisites

- **Java 21** or higher
- **Gradle 9.2.1** or higher
- **MySQL 8** or higher
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   cd /Users/harry/project/zeroq/zeroq-common
   ```

2. **Install dependencies**
   ```bash
   ./gradlew build -x test
   ```

## Building & Running

### Build Commands

```bash
# Build entire project with tests
./gradlew build

# Build without running tests
./gradlew build -x test

# Clean build artifacts
./gradlew clean

# Assemble JAR files only
./gradlew assemble
```

### Running the Service

```bash
# Run the backend service
./gradlew zeroq-back-service:bootRun

# Service will start on: http://localhost:8080
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew zeroq-back-service:test

# Run single test
./gradlew zeroq-back-service:test --tests com.zeroq.back.SomeTest
```

## API Endpoints

### Base URL: `http://localhost:8080/api/v1`

### 1. Authentication Service (`/auth`)

| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/auth/signup` | User registration | `SignUpRequest` | `TokenResponse` |
| POST | `/auth/login` | User login | `LoginRequest` | `TokenResponse` |
| POST | `/auth/refresh` | Refresh access token | Bearer token | `TokenResponse` |
| GET | `/auth/health` | Health check | - | `String` |

**Example:**
```bash
# Sign up
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe",
    "phoneNumber": "010-1234-5678"
  }'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

---

### 2. User Management (`/users`, `/favorites`)

#### User Profile

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---|
| GET | `/users/profile` | Get current user profile | Yes |
| PUT | `/users/profile` | Update user profile | Yes |
| DELETE | `/users/profile` | Delete user account | Yes |
| GET | `/users/{userId}` | Get specific user (Admin only) | Admin |

**Example:**
```bash
# Get current user profile
curl -X GET http://localhost:8080/api/v1/users/profile \
  -H "Authorization: Bearer {access_token}"

# Update profile
curl -X PUT http://localhost:8080/api/v1/users/profile \
  -H "Authorization: Bearer {access_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "phoneNumber": "010-9876-5432"
  }'
```

#### Favorites

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---|
| GET | `/favorites?page=0&size=20` | Get user's favorites | Yes |
| POST | `/favorites/{spaceId}` | Add space to favorites | Yes |
| DELETE | `/favorites/{spaceId}` | Remove from favorites | Yes |

**Example:**
```bash
# Get favorites
curl -X GET "http://localhost:8080/api/v1/favorites?page=0&size=20" \
  -H "Authorization: Bearer {access_token}"

# Add favorite
curl -X POST http://localhost:8080/api/v1/favorites/1 \
  -H "Authorization: Bearer {access_token}"
```

---

### 3. Space Management (`/spaces`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---|
| GET | `/spaces?page=0&size=20` | List all spaces (paginated) | No |
| GET | `/spaces/{id}` | Get space details | No |
| GET | `/spaces/category/{categoryId}?page=0&size=20` | Filter by category | No |
| GET | `/spaces/search?keyword=...&page=0&size=20` | Search spaces | No |
| GET | `/spaces/top-rated?page=0&size=20` | Get top-rated spaces | No |
| POST | `/spaces` | Create new space | Admin |
| PUT | `/spaces/{id}` | Update space | Admin |
| DELETE | `/spaces/{id}` | Delete space | Admin |

**Example:**
```bash
# List all spaces
curl -X GET "http://localhost:8080/api/v1/spaces?page=0&size=20"

# Search spaces
curl -X GET "http://localhost:8080/api/v1/spaces/search?keyword=cafe&page=0&size=20"

# Get space details
curl -X GET http://localhost:8080/api/v1/spaces/1

# Create space (Admin)
curl -X POST http://localhost:8080/api/v1/spaces \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Starbucks",
    "description": "Coffee shop",
    "categoryId": 1,
    "capacity": 50
  }'
```

---

### 4. Occupancy Tracking (`/occupancy`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---|
| GET | `/occupancy/spaces/{spaceId}` | Get current occupancy | No |
| GET | `/occupancy/spaces/{spaceId}/history?page=0&size=20` | Get occupancy history | No |
| GET | `/occupancy/spaces/{spaceId}/average?days=7` | Get average occupancy | No |

**Example:**
```bash
# Get current occupancy
curl -X GET http://localhost:8080/api/v1/occupancy/spaces/1

# Get occupancy history
curl -X GET "http://localhost:8080/api/v1/occupancy/spaces/1/history?page=0&size=20"

# Get average occupancy (last 7 days)
curl -X GET "http://localhost:8080/api/v1/occupancy/spaces/1/average?days=7"
```

---

### 5. Reviews & Ratings (`/reviews`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---|
| GET | `/reviews/spaces/{spaceId}?page=0&size=20` | Get reviews for space | No |
| GET | `/reviews/users/{userId}?page=0&size=20` | Get user's reviews | No |
| POST | `/reviews/spaces/{spaceId}` | Create review | Yes |
| DELETE | `/reviews/{reviewId}` | Delete review | Yes |
| GET | `/reviews/spaces/{spaceId}/rating` | Get average rating | No |

**Example:**
```bash
# Get reviews for space
curl -X GET "http://localhost:8080/api/v1/reviews/spaces/1?page=0&size=20"

# Create review
curl -X POST http://localhost:8080/api/v1/reviews/spaces/1 \
  -H "Authorization: Bearer {access_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Great place!",
    "content": "Clean and spacious",
    "rating": 5
  }'

# Get average rating
curl -X GET http://localhost:8080/api/v1/reviews/spaces/1/rating
```

---

## Authentication

### JWT Token Flow

1. **Sign Up / Login**: Send credentials to receive `AccessToken` and `RefreshToken`
2. **Include Token**: Add token to Authorization header for protected endpoints
3. **Refresh Token**: Use refresh token to get new access token when expired

### Authorization Header

```bash
Authorization: Bearer {access_token}
```

### Token Expiration

- **Access Token**: 24 hours
- **Refresh Token**: 7 days

### Example Protected Request

```bash
curl -X GET http://localhost:8080/api/v1/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## Configuration

### Environment Profiles

The application supports multiple profiles for different environments:

```bash
# Development (default)
./gradlew zeroq-back-service:bootRun --args='--spring.profiles.active=local'

# Testing
./gradlew zeroq-back-service:bootRun --args='--spring.profiles.active=test'

# Development server
./gradlew zeroq-back-service:bootRun --args='--spring.profiles.active=dev'

# Production
./gradlew zeroq-back-service:bootRun --args='--spring.profiles.active=prod'
```

### Configuration Files

- `src/main/resources/application.yml` - Default configuration
- `src/main/resources/application-local.yml` - Local development settings
- `src/main/resources/application-dev.yml` - Development environment settings
- `src/main/resources/application-test.yml` - Test environment settings
- `src/main/resources/application-prod.yml` - Production environment settings

### Key Properties

```yaml
spring:
  application:
    name: zeroq-service-back
  jpa:
    hibernate:
      ddl-auto: validate  # Manual schema management required
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss

jwt:
  secret: lifespacesecretkeyforjwt256bitminimumlengthrequired
  expiration: 86400000      # 24 hours in milliseconds
  refresh-expiration: 604800000  # 7 days in milliseconds

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
      base-path: /actuator
```

### Database Configuration

The service uses MySQL 8 with Master-Slave replication:

- **DDL Auto**: `validate` (manual schema management)
- **Hibernate Batch Size**: 20
- **Hibernate Fetch Size**: 50
- **Show SQL**: false
- **Format SQL**: true

---

## Error Handling

### Standard Error Response

```json
{
  "success": false,
  "code": "NOT_FOUND",
  "message": "Space not found",
  "status": 404,
  "timestamp": "2024-01-26T10:30:00Z",
  "path": "/api/v1/spaces/999"
}
```

### Common Error Codes

| Status | Code | Description |
|--------|------|-------------|
| 400 | BAD_REQUEST | Invalid request parameters |
| 401 | UNAUTHORIZED | Missing or invalid token |
| 403 | FORBIDDEN | Insufficient permissions |
| 404 | NOT_FOUND | Resource not found |
| 409 | CONFLICT | Resource already exists |
| 500 | INTERNAL_ERROR | Server error |

---

## Performance Optimization

- **Pagination**: All list endpoints support `page` and `size` query parameters
- **Lazy Loading**: ManyToOne relationships use `FetchType.LAZY`
- **Batch Operations**: JPA batch size configured to 20
- **Index Optimization**: Frequently queried fields are indexed in database

---

## Dependencies

### Spring Boot Starters
- `spring-boot-starter-web` - Web framework
- `spring-boot-starter-data-jpa` - Database ORM
- `spring-boot-starter-security` - Security framework
- `spring-boot-starter-validation` - Input validation
- `spring-boot-starter-actuator` - Monitoring & metrics

### Database & ORM
- `MySQL Connector/J 8.3.0` - MySQL driver
- `Spring Data JPA` - JPA implementation

### Authentication
- `jjwt-api 0.12.3` - JWT creation
- `jjwt-impl 0.12.3` - JWT implementation
- `jjwt-jackson 0.12.3` - JWT Jackson support

### Utilities
- `Lombok 1.18.30` - Boilerplate reduction

---

## Troubleshooting

### Issue: Port 8080 already in use
```bash
# Change port in application.yml
server:
  port: 8081
```

### Issue: Database connection error
```bash
# Check MySQL is running and credentials in application-{profile}.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/zeroq
    username: root
    password: password
```

### Issue: JWT token validation failed
```bash
# Ensure JWT secret is configured correctly
jwt:
  secret: lifespacesecretkeyforjwt256bitminimumlengthrequired
```

---

## Contributing

When adding new features, follow the architecture pattern:

1. **Entity** → `database/pub/entity/{Entity}.java`
2. **Repository** → `database/pub/repository/{Entity}Repository.java`
3. **DTO** → `database/pub/dto/{Entity}DTO.java`
4. **Service** → `service/{domain}/biz/{Domain}Service.java`
5. **Controller** → `service/{domain}/act/{Domain}Controller.java`

See [CLAUDE.md](../CLAUDE.md) for detailed architecture guidelines.

---

## License

Proprietary - All rights reserved

---

## Support

For issues or questions, contact the development team or check the project documentation.

**Last Updated**: 2024-01-26
