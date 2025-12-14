# System Architecture

## 1. 아키텍처 개요 (Layered Architecture)
본 프로젝트는 Spring Boot의 전형적인 **Layered Architecture**를 따릅니다.

```mermaid
graph TD
    Client[Client (Web/Mobile)]
    
    subgraph "Presentation Layer"
        Controller[Controller]
        DTO[DTO (Request/Response)]
    end
    
    subgraph "Business Layer"
        Service[Service]
        Domain[Domain Model]
    end
    
    subgraph "Data Access Layer"
        Repository[Repository (JPA/QueryDSL)]
        Entity[Entity]
    end
    
    subgraph "Infrastructure"
        DB[(MySQL)]
        Cache[(Redis)]
    end

    Client --> |HTTP| Controller
    Controller --> |DTO| Service
    Service --> |Method Call| Repository
    Repository --> |SQL| DB
    Repository --> |SQL| DB
    Service -.-> |Cache Read/Write| Cache
```

## 2. 계층별 역할

### Presentation Layer (`wsd.bookstore.*.controller`)
- **역할**: 외부 요청을 받아 비즈니스 로직으로 전달하고, 처리 결과를 응답합니다.
- **주요 구성**:
    - `Controller`: 핸들러 매핑, 요청/응답 처리
    - `GlobalExceptionHandler`: 전역 예외 처리
    - `SecurityConfig`: 인증/인가 필터링

### Business Layer (`wsd.bookstore.*.service`)
- **역할**: 핵심 비즈니스 로직을 수행하고 트랜잭션을 관리합니다.
- **주요 구성**:
    - `Service`: 트랜잭션 단위(`@Transactional`)의 업무 로직
    - `Mapper`: Entity <-> DTO 변환

### Data Access Layer (`wsd.bookstore.*.repository`)
- **역할**: 데이터베이스에 접근하여 데이터를 영속화하거나 조회합니다.
- **주요 구성**:
    - `JpaRepository`: 기본적인 CRUD 제공
    - `QueryDSL`: 복잡한 검색 조건 및 동적 쿼리 처리

## 3. 기술 스택 (Tech Stack)

### Backend
- **Framework**: Spring Boot 3.5.8
- **Language**: Java 21
- **Build Tool**: Gradle

### Database
- **RDBMS**: MySQL 8.0+ (Main DB)
- **NoSQL**: Redis (Cache (Category, BestSeller) & Session Store, JWT Refresh Token)

### Security
- **Authentication**: JWT (Access Token, Refresh Token)
- **Authorization**: Spring Security (Role-based)

### Infrastructure
- **Deployment**: JCloud
- **Container**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
