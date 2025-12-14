# Infrastructure & Credentials
MySQL, Redis에 접속하는 방법

## 1. Server Access (SSH)
가장 먼저 서버에 접속해야 합니다.

- **Host**: `113.198.66.75`
- **Port**: `19192`
- **User**: `ubuntu`
- **Key**: `pbh.pem`

### 접속 명령어 (Terminal)
```bash
ssh -i pbh.pem ubuntu@113.198.66.75 -p 19192
```

---

## 2. Database (MySQL)
**SSH 접속 후**, 서버 내부에서 DB에 접속합니다.

- **Container Name**: `bookstore-db`
- **Database**: `bookstore`
- **Username**: `qkrqudgns`
- **Password**: `qkrqudgns12!@`

### 접속 방법 (Server Internal)
```bash
# 1. 컨테이너 확인
docker ps | grep db

# 2. 접속
docker exec -it bookstore-db mysql -u qkrqudgns -p
```
*비밀번호 입력 프롬프트가 뜨면 `qkrqudgns12!@`를 입력하세요.*

---

## 3. Redis
**SSH 접속 후**, 서버 내부에서 Redis에 접속합니다.

- **Container Name**: `bookstore-redis`
- **Password**: `qkrqudgns12!@`

### 접속 방법 (Server Internal)
```bash
# 1. 컨테이너 확인
docker ps | grep redis

# 2. 접속
docker exec -it bookstore-redis redis-cli
```

*비밀번호 입력 프롬프트가 뜨면 `qkrqudgns12!@`를 입력하세요.*

---

## 4. Test Accounts (Application)
초기 데이터(Seed)로 생성된 테스트 계정입니다.

### Admin (관리자)
- **ID (Email)**: `admin@example.com`
- **Password**: `P@ssw0rd!`

### User (사용자)
- **ID (Email)**: `user1@example.com`
- **Password**: `P@ssw0rd!`
