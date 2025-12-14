# Bookstore Backend (실전 API 서버 구현)

온라인 서점(Bookstore)을 위한 백엔드 API 서버입니다.  
사용자는 도서를 검색/주문/리뷰할 수 있고, 관리자는 도서 및 메타데이터(작가/카테고리/출판사) 등을 관리합니다.

- Swagger(OpenAPI) 자동 문서화
- JWT 인증/인가 + RBAC(ROLE_USER / ROLE_ADMIN)
- MySQL(FK/Index) + Flyway 마이그레이션/시드
- Redis 캐싱(일부 조회 성능 개선)
- Postman 컬렉션(환경변수/스크립트 포함)
- 자동화 테스트(20+)
- JCloud 배포 및 헬스체크 제공

---

## 1) 배포 주소 (JCloud)

| 구분 | URL |
| --- | --- |
| **Base URL (API Root)** | `http://113.198.66.75:18192` |
| **Swagger UI** | `http://113.198.66.75:18192/swagger-ui/index.html` |
| **Health Check (무인증)** | `http://113.198.66.75:18192/health` |

---

## 2) 기술 스택

- **Framework**: Spring Boot
- **DB**: MySQL (FK/Index 적용)
- **Cache**: Redis
- **Migration/Seed**: Flyway
- **Auth**: JWT (Access/Refresh) + RBAC
- **Docs**: Swagger(OpenAPI)
- **Deploy**: JCloud (Docker Compose 기반)

---

## 3) 프로젝트 구조(예시)

```

repo-root
├─ README.md
├─ .gitignore
├─ .env.example
├─ docker-compose.yml
├─ docs/
│  ├─ api-design.md
│  ├─ db-schema.(png|sql|md)
│  └─ architecture.md
├─ postman/
│  └─ bookstore.postman_collection.json
└─ src/
└─ main/...

````

---

## 4) 실행 방법

### 4-1. 로컬 실행 (Docker Compose, 권장)

1) 환경변수 파일 생성
```bash
cp .env.example .env
# .env 값 채우기 (비밀번호/시크릿은 로컬에서만)
````

2. 실행

```bash
docker-compose up -d --build
```

3. 접속

* API Root: `http://localhost:8080`
* Swagger UI: `http://localhost:8080/swagger-ui/index.html`
* Health: `http://localhost:8080/health`

### 4-2. 로컬 실행 (Manual - Gradle)

1. DB/Redis만 실행

```bash
docker-compose up -d db redis
```

2. 빌드 및 실행

```bash
./gradlew clean build
./gradlew bootRun
```

---

## 5) 주요 환경변수(.env) 설명


| 변수명                         | 설명                 | 예시                                            |
| --------------------------- | ------------------ | --------------------------------------------- |
| `SERVER_PORT`               | 서버 포트              | `8080`                                        |
| `TZ`                        | 타임존                | `Asia/Seoul`                                  |
| `SPRING_PROFILES_ACTIVE`    | 프로파일               | `prod`                                        |
| `MYSQL_URL`                 | MySQL JDBC URL     | `jdbc:mysql://db:3306/bookstore`              |
| `MYSQL_USER`                | DB 계정              | `bookstore`                                   |
| `MYSQL_PASSWORD`            | DB 비밀번호            | (비밀)                                          |
| `MYSQL_ROOT_PASSWORD`       | DB 루트 비밀번호         | (비밀)                                          |
| `REDIS_HOST`                | Redis Host         | `redis`                                       |
| `REDIS_PASSWORD`            | Redis 비밀번호         | (비밀)                                          |
| `JWT_SECRET`                | JWT 서명키(32자 이상 권장) | (비밀)                                          |
| `JWT_ACCESS_EXPIRATION_MS`  | Access 만료(ms)      | `900000`                                      |
| `JWT_REFRESH_EXPIRATION_MS` | Refresh 만료(ms)     | `604800000`                                   |
| `CORS_ALLOWED_ORIGINS`      | 허용 Origin 목록       | `http://localhost:3000,http://localhost:8080` |

---

## 6) DB (MySQL) / 마이그레이션 / 시드

* **FK 적용**: 관계 데이터 무결성 유지
* **Index 적용**: 검색/조인 빈도가 높은 컬럼 인덱싱
* **Flyway 사용**: 스키마/시드 버전 관리
* **시드 데이터 200+건 이상**: 페이징/검색/통계 검증 가능

### DB 접속 방법 (SSH -> Docker)

1. **SSH 접속**
   ```bash
   ssh -i pbh.pem -p 19192 ubuntu@113.198.66.75
   ```

2. **MySQL 접속 (Docker)**
   ```bash
   # 1) DB 컨테이너 ID/이름 확인
   docker ps | grep db

   # 2) MySQL 접속 (예: bookstore-db)
   docker exec -it bookstore-db mysql -u bookstore -p
   ```

---

## 7) 인증(Authentication) & 인가(Authorization)

### 7-1. 인증 플로우

1. `POST /api/auth/login` (email/password)
2. Access Token 발급(Authorization 헤더 등)
3. 보호 API 호출 시
   `Authorization: Bearer <accessToken>`
4. Access 만료 시 `POST /api/auth/reissue`로 재발급(Refresh 기반)

### 7-2. Role / 권한표

| API 영역                      | ROLE_USER | ROLE_ADMIN |
| --------------------------- | --------- | ---------- |
| 내 정보(/api/users/me)         | ✅         | ✅          |
| 장바구니/주문/리뷰/즐겨찾기             | ✅         | ✅          |
| 도서/작가/카테고리/출판사 **등록/수정/삭제** | ❌         | ✅          |


---

## 8) 예제 계정 (Seed)

| 구분    | 이메일                 | 비밀번호        |
| ----- | ------------------- | ----------- |
| USER  | `user1@example.com` | `P@ssw0rd!` |
| ADMIN | `admin@example.com` | `P@ssw0rd!` |

---

## 9) 엔드포인트 요약

> 전체 목록과 요청/응답 스키마는 Swagger UI 참고
> `.../swagger-ui/index.html`

### 9-1. System

* `GET /health` (무인증)

### 9-2. Auth

* `POST /api/auth/signup`
* `POST /api/auth/login`
* `POST /api/auth/reissue`
* `POST /api/auth/logout`

### 9-3. Users

* `GET /api/users/me`
* `PUT /api/users/me`
* `PATCH /api/users/me/password`
* `DELETE /api/users/me`

### 9-4. Books / Reviews

* `GET /api/books` (검색/필터/정렬/페이지네이션)
* `GET /api/books/{id}`
* `POST /api/books` (ADMIN)
* `PUT /api/books/{id}` (ADMIN)
* `DELETE /api/books/{id}` (ADMIN)
* `GET /api/books/{bookId}/reviews` (페이지네이션)
* `POST /api/books/{bookId}/reviews`
* `GET /api/reviews/me` (페이지네이션)
* `PUT /api/reviews/{reviewId}`
* `DELETE /api/reviews/{reviewId}`
* `POST /api/reviews/{reviewId}/likes`
* `DELETE /api/reviews/{reviewId}/likes`

### 9-5. Authors / Categories / Publishers (ADMIN CUD)

* Authors: `GET/POST /api/authors`, `GET/PUT/DELETE /api/authors/{authorId}`
* Categories: `GET/POST /api/categories`, `GET/PUT/DELETE /api/categories/{categoryId}`
* Publishers: `GET/POST /api/publishers`, `GET/PUT/DELETE /api/publishers/{publisherId}`

### 9-6. Carts / Orders

* Cart:

  * `GET /api/carts/my`
  * `POST /api/carts/items`
  * `PATCH /api/carts/items/{cartItemId}`
  * `DELETE /api/carts/items/{cartItemId}`
  * `DELETE /api/carts/my` (비우기)
* Orders:

  * `POST /api/orders/checkout`
  * `GET /api/orders/me` (페이지네이션)
  * `GET /api/orders/{orderId}`
  * `DELETE /api/orders/{orderId}` (취소)

### 9-7. Favorites / Wishlist

* Favorites:

  * `GET /api/favorites`
  * `POST /api/favorites/{bookId}`
  * `DELETE /api/favorites/{bookId}`
* Wishlist:

  * `GET /api/wishlist`
  * `POST /api/wishlist/{bookId}`
  * `DELETE /api/wishlist/{bookId}`

---

## 10) 에러 응답 규격

모든 에러는 일관된 JSON 포맷을 따릅니다.

```json
{
  "timestamp": "2025-12-14T10:00:00Z",
  "path": "/api/users/me",
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "인증이 필요합니다.",
  "details": {}
}
```

### 10-1. 표준 에러 코드 (Standard Error Codes)

| HTTP | Code | Description |
| :--- | :--- | :--- |
| **400** | `BAD_REQUEST` | 잘못된 요청 형식 |
| 400 | `VALIDATION_FAILED` | 필드 유효성 검사 실패 (details 포함) |
| 400 | `INVALID_QUERY_PARAM` | 쿼리 파라미터 오류 |
| **401** | `UNAUTHORIZED` | 인증 토큰 없음 또는 유효하지 않음 |
| 401 | `TOKEN_EXPIRED` | 토큰 만료 |
| **403** | `FORBIDDEN` | 접근 권한 부족 (Role 불일치) |
| **404** | `RESOURCE_NOT_FOUND` | 리소스를 찾을 수 없음 |
| 404 | `USER_NOT_FOUND` | 사용자 정보 없음 |
| **409** | `DUPLICATE_RESOURCE` | 중복된 데이터 존재 (이메일 등) |
| 409 | `STATE_CONFLICT` | 리소스 상태 충돌 |
| **422** | `UNPROCESSABLE_ENTITY` | 비즈니스 로직 처리 불가 |
| **429** | `TOO_MANY_REQUESTS` | 요청 한도 초과 |
| **500** | `INTERNAL_SERVER_ERROR` | 서버 내부 오류 |

---

## 11) Postman 컬렉션

* 위치: `postman/bookstore.postman_collection.json`
* 환경변수 사용: `baseUrl`, `accessToken`, `refreshToken` 등
* Pre-request/Test 스크립트 포함:

  * 로그인 후 토큰 저장
  * Authorization 자동 주입
  * 대표 성공/실패 케이스 응답 검증

---

## 12) 테스트

* 목표: **20개 이상 자동화 테스트**
* 실행:

```bash
./gradlew test
```

* 인증/인가(401/403), Validation 실패(400/422), Not Found(404) 등 성공/실패 케이스 모두 포함

---

## 13) 보안/성능 고려사항

* 비밀번호 해시: BCrypt
* JWT Secret, DB 비밀번호 등은 **환경변수로만 관리**
* CORS 허용 Origin 제한
* Redis 캐싱으로 일부 조회 성능 개선
* 인덱스 적용 + N+1 방지(지연로딩/Fetch Join/Batch Size 등)
* 요청 크기 제한: 10MB (Multipart/Request Size Limit 적용)

---

## 14) 한계 및 개선 계획

* 결제 연동: 현재는 체크아웃(모의 결제) 중심 → 실제 PG 연동 개선 가능
* 모니터링: 요청 수/지연시간/에러율 지표 추가
