# API Design (Assignment 2)

## 1. 개요
본 문서는 **과제 1**에서 수립한 API 설계를 기반으로, **과제 2(실제 구현)** 과정에서 반영된 사항과 수정된 내역을 정리하고 현재 구현된 최종 API 스펙을 기술합니다.

---

## 2. 과제 1 설계 vs 과제 2 구현 차이점 요약

### 2.1. 제외된 기능
다음 기능은 과제 2 **실제 구현 범위에서 제외**되었습니다.
- **댓글 (Comments) 관련 API 전체**: (Op 39 ~ 45)
  - 리뷰 기능과 역할이 중복된다고 판단하여 제외하였습니다.
- **내가 좋아요한 리뷰 목록**: (Op 10)
  - 실제 사용 시나리오에서 필요성이 낮다고 판단하여 제외하였습니다.

### 2.2. 변경된 사양
- **도서 수정 (Update Book)**
  - 설계: `PATCH` (부분 수정)
  - 구현: `PUT` (`/api/books/{id}`)
  - 사유: 관리자 기능으로, 도서 정보의 대부분을 한 번에 수정하는 케이스가 많아 전체 업데이트(`PUT`) 로직으로 구현되었습니다.

---

## 3. 최종 구현 API 명세

### 3.1. 인증 (Auth)
| Method | URI | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/signup` | 회원가입 | Anonymous |
| POST | `/api/auth/login` | 로그인 (JWT Access/Refresh 발급) | Anonymous |
| POST | `/api/auth/logout` | 로그아웃 (Refresh Token 삭제) | User, Admin |
| POST | `/api/auth/reissue` | 토큰 재발급 | Anonymous (Cookie) |

### 3.2. 사용자 (User) - `/api/users`
| Method | URI | Description | Auth |
|---|---|---|---|
| GET | `/me` | 내 프로필 조회 | User, Admin |
| PUT | `/me` | 내 프로필 기본 정보 수정 | User, Admin |
| PATCH | `/me/password` | 비밀번호 변경 | User, Admin |
| DELETE | `/me` | 회원 탈퇴 | User, Admin |

### 3.3. 도서 (Book) - `/api/books`
| Method | URI | Description | Auth |
|---|---|---|---|
| GET | `/` | 도서 검색 (검색어, 카테고리, 정렬, 페이징) | Anonymous |
| GET | `/best-sellers` | 인기 도서(베스트셀러) Top 10 조회 | Anonymous |
| GET | `/{id}` | 도서 상세 조회 | Anonymous |
| POST | `/` | 도서 등록 | Admin |
| PUT | `/{id}` | 도서 정보 수정 | Admin |
| DELETE | `/{id}` | 도서 삭제 | Admin |

### 3.4. 리뷰 (Review) - `/api`
| Method | URI | Description | Auth |
|---|---|---|---|
| GET | `/books/{bookId}/reviews` | 특정 도서 리뷰 목록 조회 | Anonymous |
| POST | `/books/{bookId}/reviews` | 리뷰 작성 | User |
| GET | `/reviews/me` | 내가 작성한 리뷰 목록 조회 | User |
| PUT | `/reviews/{reviewId}` | 리뷰 수정 | User (Owner) |
| DELETE | `/reviews/{reviewId}` | 리뷰 삭제 | User (Owner) |
| POST | `/reviews/{reviewId}/likes` | 리뷰 좋아요 | User |
| DELETE | `/reviews/{reviewId}/likes` | 리뷰 좋아요 취소 | User |

### 3.5. 주문 (Order) - `/api/orders`
| Method | URI | Description | Auth |
|---|---|---|---|
| POST | `/checkout` | 주문 생성 (장바구니 기반 체크아웃) | User |
| GET | `/me` | 내 주문 목록 검색/조회 | User |
| GET | `/{orderId}` | 주문 상세 조회 | User (Owner) |
| DELETE | `/{orderId}` | 주문 취소 | User (Owner) |

### 3.6. 장바구니 (Cart) - `/api/carts`
| Method | URI | Description | Auth |
|---|---|---|---|
| GET | `/my` | 내 장바구니 조회 | User |
| POST | `/items` | 장바구니에 아이템 추가 | User |
| PATCH | `/items/{cartItemId}` | 아이템 수량 변경 | User |
| DELETE | `/items/{cartItemId}` | 아이템 삭제 | User |
| DELETE | `/my` | 장바구니 비우기 | User |

### 3.7. 즐겨찾기 (Favorite) - `/api/favorites`
| Method | URI | Description | Auth |
|---|---|---|---|
| GET | `/` | 내 즐겨찾기 목록 조회 | User |
| POST | `/{bookId}` | 즐겨찾기 추가 | User |
| DELETE | `/{bookId}` | 즐겨찾기 삭제 | User |

### 3.8. 위시리스트 (Wishlist) - `/api/wishlist`
| Method | URI | Description | Auth |
|---|---|---|---|
| GET | `/` | 내 위시리스트 목록 조회 | User |
| POST | `/{bookId}` | 위시리스트 추가 | User |
| DELETE | `/{bookId}` | 위시리스트 삭제 | User |

### 3.9. 작가 (Author) - `/api/authors`
| Method | URI | Description | Auth |
|---|---|---|---|
| GET | `/` | 작가 목록 조회 | Anonymous |
| GET | `/{authorId}` | 작가 상세 조회 | Anonymous |
| POST | `/` | 작가 등록 | Admin |
| PUT | `/{authorId}` | 작가 정보 수정 | Admin |
| DELETE | `/{authorId}` | 작가 삭제 | Admin |

### 3.10. 카테고리 (Category) - `/api/categories`
| Method | URI | Description | Auth |
|---|---|---|---|
| GET | `/` | 카테고리 목록 조회 | Anonymous |
| GET | `/{categoryId}` | 카테고리 상세 조회 | Anonymous |
| POST | `/` | 카테고리 등록 | Admin |
| PUT | `/{categoryId}` | 카테고리 수정 | Admin |
| DELETE | `/{categoryId}` | 카테고리 삭제 | Admin |

### 3.11. 출판사 (Publisher) - `/api/publishers`
| Method | URI | Description | Auth |
|---|---|---|---|
| GET | `/` | 출판사 목록 조회 | Anonymous |
| GET | `/{publisherId}` | 출판사 상세 조회 | Anonymous |
| POST | `/` | 출판사 등록 | Admin |
| PUT | `/{publisherId}` | 출판사 정보 수정 | Admin |
| DELETE | `/{publisherId}` | 출판사 삭제 | Admin |

### 3.12. 헬스 체크 (System)
- `GET /health`: 서버 상태 확인 (Anonymous)
