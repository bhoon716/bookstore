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
- **MySQL 8.0+**
- **Redis**

### 로컬 실행 (Local)

1. **저장소 클론**
   ```bash
   git clone https://github.com/bhoon716/bookstore.git
   cd bookstore
   ```

2. **환경 변수 설정**
   `.env.example` 파일을 복사하여 `.env` 파일을 생성하고 설정을 완료합니다.
   ```bash
   cp .env.example .env
   # .env 파일 편집 (DB, Redis 접속 정보, JWT Secret 등)
   ```

3. **빌드 및 실행 (Docker Compose 권장)**
   ```bash
   # 모든 서비스(App, MySQL, Redis) 실행
   docker-compose up -d --build
   ```

   **또는 수동 실행 (Manual)**
   ```bash
   # 1. 의존성 설치 및 빌드
   ./gradlew clean build -x test

   # 2. 실행
   java -jar build/libs/bookstore-0.0.1-SNAPSHOT.jar
   ```

4. **접속 확인**
   - **API 서버**: `http://localhost:8080`
   - **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

---

## 4. 환경 변수 (.env)

`.env.example` 참조. 보안상 실제 값은 커밋되지 않습니다.

| 변수명 | 설명 | 예시 |
| --- | --- | --- |
| `SERVER_PORT` | 서버 포트 | 8080 |
| `SPRING_DATASOURCE_URL` | DB 접속 URL | jdbc:mysql://db:3306/bookstore |
| `SPRING_DATASOURCE_USERNAME` | DB 계정 | root |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 | password |
| `SPRING_DATA_REDIS_HOST` | Redis 호스트 | redis |
| `SPRING_DATA_REDIS_PORT` | Redis 포트 | 6379 |
| `JWT_SECRET` | JWT 서명 비밀키 (32바이트 이상) | (Base64 Encoded) |

---

## 5. 인증 및 인가 (Auth & Roles)

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

## 6. 표준 에러 응답 (Error Response)

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

## 7. API 엔드포인트 요약

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

---

## 8. 성능 및 보안 고려사항

1. **JPA N+1 문제 해결**: `QueryDSL`의 `fetchJoin()`을 사용하여 연관된 엔티티(작가, 출판사 등)를 한 번에 조회합니다.
2. **DB 인덱싱**: 조회 빈도가 높은 `email`, `book_title` 등에 인덱스 설계를 반영했습니다.
3. **보안 강화**: `BCrypt` 패스워드 암호화 및 HttpOnly 쿠키 사용으로 XSS/탈취 위협을 완화했습니다.

---

## 9. 배포 정보 (JCloud)
- **Base URL**: `http://<JCLOUD_IP>:<PORT>`
- **Docs**: `http://<JCLOUD_IP>:<PORT>/swagger-ui/index.html`
