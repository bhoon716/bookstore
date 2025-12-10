# Bookstore API Project

## 1. 프로젝트 개요

이 프로젝트는 **온라인 서점**을 위한 백엔드 API 서버입니다. 사용자는 도서를 검색하고, 장바구니에 담아 주문할 수 있으며, 리뷰와 평점을 남길 수 있습니다. 또한, 즐겨찾기와 위시리스트 기능을 통해 관심 있는 도서를 관리할 수 있습니다.

### 주요 기능
- **사용자**: 회원가입, 로그인(JWT), 프로필 수정, 회원 탈퇴.
- **도서**: 도서 검색(키워드, 정렬, 페이징), 상세 조회.
- **주문**: 장바구니 관리, 주문 생성(체크아웃), 주문 내역 조회, 주문 취소.
- **리뷰**: 리뷰 작성, 수정, 삭제, 좋아요/취소, 내 리뷰 조회.
- **편의 기능**: 즐겨찾기, 위시리스트 관리.

---

## 2. 실행 방법

### 요구 사항
- **Java 21**
- **MySQL 8.0+**
- **Redis** (세션/캐시 용도)

### 로컬 실행
1. 저장소 클론
   ```bash
   git clone https://github.com/bhoon716/bookstore.git
   cd bookstore
   ```

2. `.env` 설정
   `.env.example` 파일을 복사하여 `.env` 파일을 생성하고, 각 환경 변수에 맞는 값을 설정합니다.
   ```bash
   cp .env.example .env
   # .env 파일 편집 (DB 접속 정보 등)
   ```

3. 빌드 및 실행
   
   이 프로젝트는 **Docker Compose**를 권장하지만, 로컬 환경에서 직접 빌드하여 실행할 수도 있습니다.
   
   #### 방법 A: Docker Compose (권장)
   애플리케이션, MySQL, Redis가 모두 자동으로 구성됩니다.

   1. **환경 변수 파일 생성**
      ```bash
      # .env.example을 복사하여 .env 생성
      cp .env.example .env
      ```
   
   2. **Docker Compose 실행**
      ```bash
      docker-compose up -d --build
      ```
   
   3. **접속**
      - **API 서버**: `http://localhost:8080`
      - **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

   #### 방법 B: 로컬 수동 실행 (Manual)
   로컬에 MySQL(3306), Redis(6379)가 실행 중이어야 합니다.

   1. **의존성 설치 및 빌드**
      ```bash
      # macOS / Linux
      ./gradlew clean build -x test
      
      # Windows
      ./gradlew.bat clean build -x test
      ```

   2. **실행**
      ```bash
      java -jar build/libs/bookstore-0.0.1-SNAPSHOT.jar
      ```
   
   3. **(선택) 마이그레이션 및 시드 데이터**
      서버 시작 시 `spring.jpa.hibernate.ddl-auto=update` 설정에 의해 스키마가 자동 관리됩니다.

---

## 3. 환경 변수 설명

`.env.example` 파일을 참고하여 아래 환경 변수를 설정해야 합니다.

| 변수명 | 설명 | 예시 |
| --- | --- | --- |
| `SERVER_PORT` | 서버 포트 | 8080 |
| `SPRING_DATASOURCE_URL` | DB 접속 URL | jdbc:mysql://localhost:3306/bookstore |
| `SPRING_DATASOURCE_USERNAME` | DB 계정 | root |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 | password |
| `SPRING_DATA_REDIS_HOST` | Redis 호스트 | localhost |
| `SPRING_DATA_REDIS_PORT` | Redis 포트 | 6379 |
| `JWT_SECRET` | JWT 서명 비밀키 (32바이트 이상) | (Base64 Encoded Key) |

---

## 4. 배포 정보 (JCloud)

- **Base URL**: `http://<JCLOUD_IP>:<PORT>`
- **Swagger UI**: `http://<JCLOUD_IP>:<PORT>/swagger-ui/index.html`
- **Health Check**: `http://<JCLOUD_IP>:<PORT>/actuator/health`

---

## 5. 인증 및 인가

### 인증 플로우 (JWT)
1. **로그인**: `POST /api/auth/login` 요청 시, 유효한 사용자라면 **Access Token**과 **Refresh Token**을 발급합니다.
   - **Access Token**: 응답 헤더 `Authorization: Bearer <token>`에 포함됩니다. (유효기간 짧음)
   - **Refresh Token**: `HttpOnly` 쿠키에 저장됩니다. (유효기간 7일)
2. **API 요청**: 보호된 리소스 접근 시, `Authorization` 헤더에 Access Token을 담아 요청합니다.
3. **토큰 재발급**: Access Token 만료 시, `POST /api/auth/reissue`를 통해 쿠키의 Refresh Token으로 새 토큰을 발급받습니다.

### 역할 및 권한

| Role | 권한 범위 | 비고 |
| --- | --- | --- |
| **ROLE_USER** | 일반 사용자 기능 (주문, 리뷰, 마이페이지 등) | 기본 가입 시 부여 |
| **ROLE_ADMIN** | 관리자 기능 (도서 등록/수정/삭제, 전체 회원 조회 등) | DB 조작 필요 |

### 예제 계정

| 구분 | 이메일 | 비밀번호 | 비고 |
| --- | --- | --- | --- |
| **사용자** | `user1@example.com` | `P@ssw0rd!` | 일반 회원 |
| **관리자** | `admin@example.com` | `P@ssw0rd!` | 관리자 권한 |

---

## 6. DB 연결 정보 (테스트)

- **Host**: `<JCLOUD_DB_HOST>`
- **Port**: `3306`
- **Database**: `bookstore`
- **User**: `test_user`
- **Password**: `test_password`

---

## 7. API 엔드포인트 요약

총 **35개**의 엔드포인트를 제공합니다.

| 도메인 | 메서드 | URL | 설명 |
| --- | --- | --- | --- |
| **Auth** | POST | `/api/auth/signup` | 회원가입 |
| | POST | `/api/auth/login` | 로그인 |
| | POST | `/api/auth/logout` | 로그아웃 |
| | POST | `/api/auth/reissue` | 토큰 재발급 |
| **User** | GET | `/api/users/me` | 내 프로필 조회 |
| | PUT | `/api/users/me` | 내 프로필 수정 |
| | PATCH | `/api/users/me/password` | 비밀번호 변경 |
| | DELETE | `/api/users/me` | 회원 탈퇴 |
| **Book** | GET | `/api/books` | 도서 검색 (Paging, Sort) |
| | GET | `/api/books/{id}` | 도서 상세 조회 |
| | POST | `/api/books` | 도서 등록 (Admin) |
| | PATCH | `/api/books/{id}` | 도서 수정 (Admin) |
| | DELETE | `/api/books/{id}` | 도서 삭제 (Admin) |
| **Order** | POST | `/api/orders/checkout` | 주문 생성 (체크아웃) |
| | GET | `/api/orders/me` | 내 주문 내역 조회 (Paging) |
| | GET | `/api/orders/{orderId}` | 주문 상세 조회 |
| | DELETE | `/api/orders/{orderId}` | 주문 취소 |
| **Cart** | GET | `/api/carts/my` | 내 장바구니 조회 |
| | POST | `/api/carts/items` | 장바구니 상품 추가 |
| | PATCH | `/api/carts/items/{itemId}` | 장바구니 상품 수량 변경 |
| | DELETE | `/api/carts/items/{itemId}` | 장바구니 상품 삭제 |
| | DELETE | `/api/carts/my` | 장바구니 비우기 |
| **Review** | GET | `/api/books/{bookId}/reviews` | 도서 리뷰 목록 조회 |
| | POST | `/api/books/{bookId}/reviews` | 리뷰 작성 |
| | GET | `/api/reviews/me` | 내가 쓴 리뷰 조회 |
| | PUT | `/api/reviews/{reviewId}` | 리뷰 수정 |
| | DELETE | `/api/reviews/{reviewId}` | 리뷰 삭제 |
| | POST | `/api/reviews/{reviewId}/likes` | 리뷰 좋아요 |
| | DELETE | `/api/reviews/{reviewId}/likes` | 리뷰 좋아요 취소 |
| **Favorite** | GET | `/api/favorites` | 즐겨찾기 목록 조회 |
| | POST | `/api/favorites/{bookId}` | 즐겨찾기 추가 |
| | DELETE | `/api/favorites/{bookId}` | 즐겨찾기 삭제 |
| **Wishlist** | GET | `/api/wishlist` | 위시리스트 목록 조회 |
| | POST | `/api/wishlist/{bookId}` | 위시리스트 추가 |
| | DELETE | `/api/wishlist/{bookId}` | 위시리스트 삭제 |

---

## 8. 성능 및 보안 고려사항

### 보안 (Security)
- **JWT 인증**: Stateless한 세션 관리를 위해 JWT를 사용하며, Refresh Token은 보안 쿠키(HttpOnly, Secure)로 관리하여 XSS 공격을 방지합니다.
- **비밀번호 암호화**: `BCrypt` 해시 알고리즘을 사용하여 비밀번호를 안전하게 저장합니다.
- **입력 검증**: `@Valid` 어노테이션을 사용하여 모든 요청 데이터에 대해 철저한 유효성 검사를 수행합니다.

### 성능 (Performance)
- **QueryDSL**: 복잡한 동적 쿼리(도서 검색 등)를 효율적으로 처리하기 위해 QueryDSL을 사용했습니다. N+1 문제를 방지하기 위해 `fetchJoin()`을 적절히 활용했습니다.
- **인덱싱**: 검색 성능 향상을 위해 주요 조회 컬럼(ISBN, User ID 등)에 DB 인덱스를 적용할 예정입니다.
- **페이지네이션**: 대용량 데이터 조회를 고려하여 모든 목록 조회 API에 페이지네이션을 적용했습니다.

---

## 9. 한계 및 개선 계획

- **캐싱**: Redis를 활용하여 '베스트셀러'나 '카테고리 목록' 등 자주 조회되는 데이터에 대한 캐싱을 적용할 예정입니다.
- **테스트 커버리지**: 현재 핵심 기능 위주로 테스트가 작성되어 있으며, 향후 엣지 케이스에 대한 테스트를 보강할 계획입니다.
- **CI/CD**: GitHub Actions를 통한 자동 배포 파이프라인을 구축할 예정입니다.
