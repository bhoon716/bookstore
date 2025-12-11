# Bookstore API Project

## 1. 프로젝트 개요

이 프로젝트는 **온라인 서점**을 위한 백엔드 API 서버입니다. 사용자는 도서 검색, 주문, 리뷰 작성 등의 활동을 할 수 있으며, 관리자는 도서와 관련된 메타 데이터(작가, 카테고리, 출판사)를 관리할 수 있습니다.

### 주요 기능
- **사용자**: 회원가입, 로그인(JWT), 프로필 수정, 회원 탈퇴.
- **도서**: 도서 검색(키워드, 정렬, 페이징), 상세 조회.
- **주문**: 장바구니 관리(Lazy Creation), 주문 생성(체크아웃), 주문 내역 조회, 주문 취소.
- **리뷰**: 리뷰 작성, 수정, 삭제, 좋아요/취소, 내 리뷰 조회.
- **관리자(Admin)**: 작가, 카테고리, 출판사, 도서 생성/수정/삭제.
- **편의 기능**: 즐겨찾기, 위시리스트 관리.

---

## 2. 기술 스택 (Tech Stack)

| 구분 | 기술 |
| --- | --- |
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.4.0 |
| **Database** | MySQL 8.0+ |
| **ORM** | Spring Data JPA, **QueryDSL** (동적 쿼리) |
| **Cache/Session** | **Redis** (Refresh Token, 캐싱 예정) |
| **Security** | Spring Security, **JWT** (Access/Refresh Token) |
| **Build/Deploy** | Gradle, **Docker**, Docker Compose |

---

## 3. 실행 방법 (Execution)

### 요구 사항
- **Java 21**
- **Docker & Docker Compose**
- **MySQL 8.0+** (로컬 직접 실행 시)
- **Redis** (로컬 직접 실행 시)

### 1. 로컬 개발 환경 (Local Development)
기본적으로 `application.properties` 설정을 따르며, **H2 Database (In-Memory)** 와 **Local Redis**를 사용합니다.

1. **Redis 실행** (필수)
   로컬에 Redis가 설치되어 있거나 Docker로 실행 중이어야 합니다.
   ```bash
   docker run -d --name redis -p 6379:6379 redis:alpine
   ```

2. **애플리케이션 실행**
   ```bash
   ./gradlew clean bootRun
   ```
   - 서버: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`
   - Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 2. 프로덕션 배포 환경 (Production / Docker)
`application-prod.properties` 설정을 따르며, **MySQL**과 **Redis** 컨테이너를 함께 실행합니다.

1. **환경 변수 설정**
   `.env.example` 파일을 복사하여 `.env` 파일을 생성하고, 실제 운영 환경 값으로 수정합니다.
   ```bash
   cp .env.example .env
   vi .env # SECRET_KEY, DB_PASSWORD 등 수정
   ```

2. **Docker Compose 실행**
   ```bash
   docker-compose up -d --build
   ```

---

## 4. 환경 변수 (.env)

배포 시 `docker-compose` 및 CI/CD 파이프라인에서 사용되는 주요 환경 변수입니다.

| 변수명 | 설명 | 기본값/예시 |
| --- | --- | --- |
| `SERVER_PORT` | 컨테이너 내부 Spring Boot 포트 | 80 |
| `TZ` | 서버 타임존 | Asia/Seoul |
| `MYSQL_USER` / `_PASSWORD` | DB 계정 및 비밀번호 | bookstore / password |
| `MYSQL_ROOT_PASSWORD` | DB Root 비밀번호 | password |
| `SPRING_DATASOURCE_URL` | DB 접속 URL (Internal) | jdbc:mysql://db:3306/bookstore... |
| `SPRING_DATA_REDIS_HOST` | Redis 호스트 (Internal) | redis |
| `JWT_SECRET` | JWT 서명 비밀키 | (32byte 이상 Base64) |
| `APP_PORT_HOST` | **JCloud 호스트** -> 앱 포트 매핑 | 80 |
| `DB_PORT_HOST` | **JCloud 호스트** -> DB 포트 매핑 | 3000 |
| `REDIS_PORT_HOST` | **JCloud 호스트** -> Redis 포트 매핑 | 8080 |

---

## 5. CI/CD 파이프라인 (GitHub Actions)

이 프로젝트는 **GitHub Actions**를 통해 자동화된 빌드 및 배포 파이프라인을 구축했습니다 (`.github/workflows/cicd.yml`).

### Workflow 개요
1. **CI (Continuous Integration)**:
   - `main` 브랜치 PR 및 Push 시 트리거.
   - Java 21 환경에서 Gradle Build (Test, Lint 포함).
   - MySQL/Redis 서비스 컨테이너 연동 테스트.
   - Docker Image Build 테스트.

2. **CD (Continuous Deployment)**:
   - `main` 브랜치 Push 시 실행 (CI 성공 후).
   - JCloud 서버에 SSH 접속.
   - 최신 코드 Pull -> `.env` 생성 (`SPRING_PROFILES_ACTIVE=prod` 자동 주입) -> `docker-compose up -d` 재배포.
   - **Health Check** 자동 수행 (배포 실패 시 롤백/에러 알림).

---

## 6. 배포 정보 (JCloud)

JCloud 서버의 포트 포워딩 설정에 따라 다음과 같이 접속합니다.

- **API Server**: `http://<JCLOUD_IP>:18xxx` (내부 80 포트로 연결)
- **Swagger Docs**: `http://<JCLOUD_IP>:18xxx/swagger-ui/index.html`
- **MySQL (External)**: `db_tool://<JCLOUD_IP>:13xxx` (내부 3000 포트)
- **Redis (External)**: `redis-cli -h <JCLOUD_IP> -p 10xxx` (내부 8080 포트)

---

## 7. 인증 및 인가 (Auth & Roles)

### 인증 플로우
1. **로그인**: `POST /api/auth/login` → `Authorization` 헤더(Access Token) + 쿠키(Refresh Token) 발급.
2. **요청**: `Authorization: Bearer <Access Token>` 헤더 포함하여 API 요청.
3. **재발급**: 401 발생 시 `POST /api/auth/reissue` 요청 → 만료된 Access Token 갱신.

### 역할 (Role)
| Role | 설명 | 접근 가능 범위 |
| --- | --- | --- |
| **ROLE_USER** | 일반 사용자 | 주문, 리뷰, 마이페이지, 조회 등 |
| **ROLE_ADMIN** | 관리자 | `/api/books`, `/api/authors`, `/api/categories` 등의 CUD(생성/수정/삭제) |

### 예제 계정
| 구분 | 이메일 | 비밀번호 |
| --- | --- | --- |
| **사용자** | `user1@example.com` | `P@ssw0rd!` |
| **관리자** | `admin@example.com` | `P@ssw0rd!` |

---

## 8. 표준 에러 응답 (Error Response)

모든 API 에러는 아래와 같은 **일관된 JSON 포맷**으로 반환됩니다.

```json
{
  "timestamp": "2025-03-05T12:34:56Z",
  "path": "/api/posts/1",
  "status": 400,
  "code": "BAD_REQUEST",
  "message": "잘못된 요청입니다.",
  "details": {
    "title": "제목은 필수입니다."
  }
}
```

### 주요 에러 코드
| HTTP | 에러 코드 | 설명 |
| --- | --- | --- |
| **400** | `BAD_REQUEST` | 요청 형식이 올바르지 않음 |
| **401** | `UNAUTHORIZED` | 인증 실패 (토큰 없음/만료) |
| **403** | `FORBIDDEN` | 권한 없음 (일반 유저가 관리자 API 접근 등) |
| **404** | `NOT_FOUND` | 리소스(도서, 유저 등)를 찾을 수 없음 |
| **409** | `DUPLICATE_RESOURCE` | 데이터 중복 (이미 가입된 이메일 등) |
| **500** | `INTERNAL_SERVER_ERROR` | 서버 내부 오류 |

---

## 9. API 엔드포인트 요약

총 **40개 이상**의 엔드포인트를 제공합니다. 자세한 스펙은 **Swagger UI**를 참고하세요.

### 인증 (Auth)
- `POST /api/auth/signup` : 회원가입
- `POST /api/auth/login` : 로그인
- `POST /api/auth/logout` : 로그아웃
- `POST /api/auth/reissue` : 토큰 재발급

### 사용자 (User)
- `GET /api/users/me` : 내 프로필 조회
- `PUT /api/users/me` : 내 프로필 수정
- `PATCH /api/users/me/password` : 비밀번호 변경
- `DELETE /api/users/me` : 회원 탈퇴

### 도서 (Book) - Public
- `GET /api/books` : 도서 검색 (Paging, Sort, Keyword)
- `GET /api/books/{id}` : 도서 상세 조회

### 관리자 (Admin Only)
- **도서 관리**
  - `POST /api/books` : 도서 등록
  - `PATCH /api/books/{id}` : 도서 수정
  - `DELETE /api/books/{id}` : 도서 삭제
- **작가 관리** (`/api/authors`)
  - `POST`, `PUT`, `DELETE` 제공
- **카테고리 관리** (`/api/categories`)
  - `POST`, `PUT`, `DELETE` 제공
- **출판사 관리** (`/api/publishers`)
  - `POST`, `PUT`, `DELETE` 제공

### 주문 (Order) & 장바구니 (Cart)
- `GET /api/carts/my` : 장바구니 조회
- `POST /api/carts/items` : 장바구니 담기 (Lazy Creation)
- `PATCH /api/carts/items/{id}` : 수량 변경
- `DELETE /api/carts/items/{id}` : 삭제
- `POST /api/orders/checkout` : 주문 결제 (체크아웃)
- `GET /api/orders/me` : 내 주문 내역
- `DELETE /api/orders/{id}` : 주문 취소

### 리뷰 (Review) & 기타
- `GET /api/books/{bookId}/reviews` : 리뷰 목록
- `POST /api/reviews` : 리뷰 작성
- `POST /api/reviews/{id}/likes` : 리뷰 좋아요
- `GET /api/favorites` : 즐겨찾기 목록
- `GET /api/wishlist` : 위시리스트 목록
