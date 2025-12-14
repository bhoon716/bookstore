# 실전 API 서버 구현 (Bookstore)

## 0. 프로젝트 개요
이 프로젝트는 **온라인 서점**을 위한 백엔드 API 서버입니다.  
사용자는 도서를 검색하고 주문하며 리뷰를 작성할 수 있고, 관리자는 도서 및 메타데이터(작가, 카테고리, 출판사)를 관리할 수 있습니다.
과제 요구사항에 맞춰 **JCloud 배포**, **Swagger 문서화**, **Flyway DB 마이그레이션**, **JWT 인증/인가** 등을 구현했습니다.

### 📌 Quick Summary
| 구분 | 내용 |
| --- | --- |
| **API Root** | `http://113.198.66.75:18192` |
| **Swagger UI** | [http://113.198.66.75:18192/swagger-ui/index.html](http://113.198.66.75:18192/swagger-ui/index.html) |
| **Admin ID** | `admin@example.com` / `P@ssw0rd!` |

### 주요 기능
- **사용자**: 회원가입, 로그인(JWT), 프로필 관리, 비밀번호 변경.
- **도서**: 검색(키워드, 정렬, 카테고리), 상세 조회.
- **주문**: 장바구니, 주문 결제(Simulated), 주문 취소, 내 주문 내역.
- **리뷰**: 평점 및 리뷰 작성, 좋아요 기능.
- **관리자**: 도서/작가/카테고리/출판사 관리(CRUD), 통계 조회.

---

## 1. 실행 방법 (How to Run)

### 로컬 실행 (Docker Compose)
Docker Compose를 사용하여 로컬에서 DB, Redis, Application을 통합 실행할 수 있습니다.

1. **환경 변수 파일 생성**:
   ```bash
   cp .env.example .env
   # .env 파일을 열어 필요한 설정을 확인하세요
   ```

2. **실행**:
   ```bash
   docker-compose up -d --build
   ```

3. **접속**:
   - **API Root**: `http://localhost:80`
   - **Swagger UI**: `http://localhost:80/swagger-ui/index.html`
   - **Health Check**: `http://localhost:80/actuator/health`

### 프로덕션 배포 환경 (JCloud)
Docker Compose를 사용하여 DB, Redis, Application을 일괄 실행합니다.

```bash
# 1. .env 설정
cp .env.example .env
vi .env # 실제 비밀번호 및 설정 입력

# 2. 실행
docker-compose up -d --build
```

---

## 2. 환경 변수 설정
`src/main/resources/application.properties` (Local) 및 `.env` (Prod)에서 사용됩니다.
`.env.example` 파일을 참고하여 `.env`를 생성하세요.

| 변수명 | 설명 | 예시 |
| --- | --- | --- |
| `SERVER_PORT` | 애플리케이션 포트 | 8080 |
| `MYSQL_URL` | DB 접속 URL | jdbc:mysql://db:3306/bookstore... |
| `MYSQL_USER` | DB 사용자 | bookstore |
| `MYSQL_PASSWORD` | DB 비밀번호 | (비밀) |
| `REDIS_HOST` | Redis 호스트 | redis |
| `JWT_SECRET` | JWT 서명 키 (32자 이상) | (비밀) |

---

## 3. 배포 주소

| 구분 | URL | 비고 |
| --- | --- | --- |
| **Base URL** | `http://113.198.66.75:18192` | API Root |
| **Swagger UI** | `http://113.198.66.75:18192/swagger-ui/index.html` | API 문서 |
| **Health Check** | `http://113.198.66.75:18192/actuator/health` | 서버 상태 |

> **JCloud 접속 정보 (SSH)**
> - IP: `113.198.66.75` (Port: `19192`)
> - User: `ubuntu`
> - Key: `pbh.pem`

---

## 4. 인증 및 인가

### 인증 플로우
1. **로그인**: `POST /api/auth/login`으로 이메일/비밀번호 전송.
2. **발급**: 유효한 경우 `AccessToken`(Header) 및 `RefreshToken`(Cookie/Body) 발급.
3. **요청**: API 요청 헤더에 `Authorization: Bearer <AccessToken>` 포함.
4. **갱신**: 401 응답 시 `POST /api/auth/reissue`로 토큰 재발급.

### 역할/권한표
| Role | 설명 | 접근 가능 API |
| --- | --- | --- |
| **ROLE_USER** | 일반 사용자 | 도서 조회, 주문, 리뷰 작성, 마이페이지 등 |
| **ROLE_ADMIN** | 관리자 | `/api/admin/**`, 도서/작가/카테고리 CUD, 통계 등 |

---

## 5. 예제 계정
초기 데이터(Seed)로 생성된 테스트 계정입니다.

| 구분 | 이메일 | 비밀번호 |
| --- | --- | --- |
| **사용자** | `user1@example.com` | `P@ssw0rd!` |
| **관리자** | `admin@example.com` | `P@ssw0rd!` |

---

## 6. DB 연결 정보
JCloud 외부에서 접속 가능한 정보입니다. (DBeaver 등 활용)

- **Host**: `113.198.66.75`
- **Port**: `13192` (Internal 3306)
- **Database**: `bookstore`
- **User/Password**: `.env 참고`

---

## 7. 엔드포인트 요약
Swagger: [http://113.198.66.75:18192/swagger-ui/index.html](http://113.198.66.75:18192/swagger-ui/index.html)

### Auth & User
| Method | URL | 설명 |
| --- | --- | --- |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/reissue` | 토큰 재발급 |
| GET | `/api/users/me` | 내 정보 조회 |
| PUT | `/api/users/me` | 내 정보 수정 |
| PATCH | `/api/users/me/password` | 비밀번호 변경 |
| DELETE | `/api/users/me` | 회원 탈퇴 |

### Book
| Method | URL | 설명 |
| --- | --- | --- |
| GET | `/api/books` | 도서 목록 (검색/정렬/페이징) |
| GET | `/api/books/{id}` | 도서 상세 조회 |
| GET | `/api/categories` | 카테고리 목록 |
| GET | `/api/books/{id}/reviews` | 도서 리뷰 목록 |

### Order & Cart
| Method | URL | 설명 |
| --- | --- | --- |
| GET | `/api/carts` | 장바구니 조회 |
| POST | `/api/carts/items` | 장바구니 담기 |
| PATCH | `/api/carts/items/{id}` | 수량 변경 |
| DELETE | `/api/carts/items/{id}` | 장바구니 삭제 |
| POST | `/api/orders` | 주문 생성 (결제) |
| GET | `/api/orders` | 내 주문 내역 |
| GET | `/api/orders/{id}` | 주문 상세 |
| POST | `/api/orders/{id}/cancel` | 주문 취소 |

### Review & Like
| Method | URL | 설명 |
| --- | --- | --- |
| POST | `/api/reviews` | 리뷰 작성 |
| PUT | `/api/reviews/{id}` | 리뷰 수정 |
| DELETE | `/api/reviews/{id}` | 리뷰 삭제 |
| POST | `/api/reviews/{id}/likes` | 리뷰 좋아요 |
| DELETE | `/api/reviews/{id}/likes` | 리뷰 좋아요 취소 |

### Admin
| Method | URL | 설명 |
| --- | --- | --- |
| POST | `/api/admin/books` | 도서 등록 |
| PUT | `/api/admin/books/{id}` | 도서 수정 |
| DELETE | `/api/admin/books/{id}` | 도서 삭제 |
| POST | `/api/admin/authors` | 작가 등록 |
| POST | `/api/admin/publishers` | 출판사 등록 |
| POST | `/api/admin/categories` | 카테고리 등록 |
| GET | `/api/admin/users` | 전체 회원 조회 |
| PATCH | `/api/admin/users/{id}/status` | 회원 상태 변경(정지) |
| GET | `/api/admin/stats/daily` | 일별 매출 통계 |
| GET | `/api/admin/stats/top-sold` | 베스트셀러 통계 |

---

## 8. 표준 에러 코드 (Error Codes)
모든 에러 응답은 `code`와 `message`를 포함한 JSON 형식을 따릅니다.

| HTTP | Code | Description |
| --- | --- | --- |
| 400 | `BAD_REQUEST` | 잘못된 요청 |
| 400 | `INVALID_INPUT` | 입력값 검증 실패 |
| 401 | `UNAUTHORIZED` | 인증 필요 |
| 403 | `FORBIDDEN` | 권한 부족 |
| 404 | `NOT_FOUND` | 리소스 없음 |
| 409 | `DUPLICATE_EMAIL` | 이메일 중복 |
| 422 | `Unprocessable Entity` | 비즈니스 로직 오류 |
| 500 | `INTERNAL_SERVER_ERROR` | 서버 오류 |

---

## 9. 성능 및 보안 고려사항
- **Security**: 비밀번호는 `BCrypt`로 해싱하여 저장하며, JWT 서명 키는 환경변수로 관리합니다.
- **Performance**:
    - 자주 조회되는 `books` 테이블에 인덱스 최적화 (`idx_books_title`, `idx_books_category`).
    - N+1 문제를 방지하기 위해 `Fetch Join` 및 `Batch Size` 적용.
- **Validation**: `@Valid`를 사용하여 입력값을 철저히 검증합니다.

## 10. 한계 및 개선 계획
- **결제 연동**: 현재는 모의 결제만 구현되어 있으며, PG사 연동이 필요합니다.
- **캐싱**: Redis 캐싱을 통한 도서 목록 조회 성능 개선 예정.
- **비동기 처리**: 이메일 발송 등은 메시지 큐(Kafka/RabbitMQ) 도입 고려.
