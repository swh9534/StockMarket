# StockMarket

> Front : Vue + Vite
> 
> 
> Back : Spring + Maven
> 
> Database : mariaDB
> 

# 개요

> 이 프로젝트는 주식 시장 데이터를 관리하고 플레이어(사용자)가 주식을 사고팔 수 있도록 설계된 시스템입니다.  Back-End는 Spring Boot와 MariaDB를 활용하여 구현되었으며, Front-End는 Vue를 활용하여 구현되었습니다. 주식 정보 및 거래 내역을 관리하는 기능을 포함합니다.
> 

### 기술 스택

- **언어:** Java 17
- **프레임워크:** Spring Boot 3.4.4
- **라이브러리:** Lombok, JPA
- **보안:** Spring Security

- **데이터베이스:** MariaDB
- **빌드 툴:** Maven
- **ORM:** Spring Data JPA

### 주요 기능

- 사용자 관리 (회원 가입, 로그인)
- 주식 정보 조회
- 플레이어 별 포트폴리오 제공

- 주식 별 주가 변동 기록 시각화
- 주식 거래 (매수, 매도)

# 프로젝트 구조

각 폴더의 역할은 다음과 같습니다.

- **controller:** 클라이언트 요청을 처리하는 REST API 컨트롤러를 포함한다.
- **domain:** 데이터베이스 테이블과 매핑되는 엔티티 클래스가 정의되어 있다.
- **dto:** 클라이언트와의 데이터 교환을 위한 DTO(Data Transfer Object)를 정의한다.
- **exception:** 애플리케이션 내에서 발생하는 예외를 처리하는 클래스가 포함된다.
- **repository:** 데이터베이스 접근을 담당하는 JPA Repository 인터페이스를 정의한다.
- **service:** 비즈니스 로직을 처리하는 서비스 계층을 구현한다.

# 데이터 모델링

| **엔티티** | **필드** | **설명** |
| --- | --- | --- |
| **User** | userId, password, player | 사용자 계정 정보 |
| **Player** | playerId, name, cash | 플레이어 정보 (사용자별 자금 관리) |
| **PlayerStock** | id, stock, quantity, investedAmount, player | 플레이어가 보유한 주식 정보 |
| **Stock** | stockId, stockName, stockPrice | 주식 종목 정보 |
| **StockHistory** | id, stockName, price, date | 주식 가격 변동 기록 |

# API 설계

### **1. 사용자 API**

---

**1.1 사용자 등록**

- **URL**: POST /api/users/register
- **RequestBody**:

```json
{
  "userId": "user1",
  "password": "pass123",
  "playerName": "Player1",
  "initialCash": 10000
}
```

- **Response**:

```json
{
  "userId": "user1",
  "playerName": "Player1",
  "cash": 10000,
  "playerId": "PLAYER1"
}

{
    "error": "Registration failed",
    "message": "이미 존재하는 ID입니다."
}
```

**1.2 로그인**

- **URL**: POST /api/users/login
- **RequestBody**:

```json
{
  "userId": "user1",
  "password": "pass123"
}
```

- **Response**:

```json
{
  "userId": "user1",
  "playerName": "Player1",
  "cash": 10000,
  "playerId": "PLAYER1"
}

{
    "error": "Authentication failed",
    "message": "비밀번호가 일치하지 않습니다."
}
```

**1.3 로그아웃**

- **URL**: POST /api/users/logout
- **Response**:

```json
{
  "message": "Logout successful"
}
```

**1.4 현재 로그인된 사용자 정보 확인**

- **URL**: GET /api/users/current
- **Response**:

```json
{
    "userId": "user1",
    "playerName": "Player1",
    "cash": 10000,
    "playerId": "PLAYER1"
}
```

**1.5 관리자 권한 확인**

- **URL**: POST /api/users/admin/check
- **RequestBody:**

```json
{
  "userId": "admin",
  "password": "1234"
}
```

- **Response**:

```json
{
    "admin": true
}
{
    "error": "Authentication failed",
    "message": "관리자 ID가 아닙니다."
}
```

**1.6 (관리자) 전체 유저 정보 확인**

- **URL**: GET /api/users/all
- **Response**:

```json
{
        "userId": "000",
        "playerName": "000",
        "cash": 1000000.0,
        "playerId": "PLAYER4",
        "totalAssets": 1000000.0,
        "admin": false
    },
    {
        "userId": "111",
        "playerName": "111",
        "cash": 500006.0,
        "playerId": "PLAYER2",
        "totalAssets": 561244.0,
        "admin": false
    },
    ...
}
```

---

### **2 Player API**

---

**2.1 플레이어 정보 조회**

- **URL**: GET /api/players/{playerId}
- **Response**:

```json
{
  "playerId": "111",
  "playerName": "Player1",
  "cash": 10000,
  "message": "Player information retrieved successfully"
}
```

**2.2 플레이어 포트폴리오 조회**

- **URL**: GET /api/players/{playerId}/portfolio
- **Response**:

```json
{
    "portfolio": [
        {
            "id": 2,
            "stock": {
                "stockId": "STOCK001",
                "stockName": "Apple",
                "stockPrice": 143
            },
            "quantity": 150,
            "investedAmount": 203739,
            "player": {
                "playerId": "PLAYER2",
                "name": "111",
                "cash": 500006
            },
            "totalValue": 21450,
            "stockName": "Apple",
            "profitRate": -89.47182424572615
        },
        ...
    ],
    "totalAssets": 562437
}
        
```

**2.3 주식 매수 or 매도**

- **URL**: POST /api/players/{playerId}/buy or sell
- **RequestBody:**

```json
{
    "stockName" : "Apple",
    "quantity" : 5
}
```

- **Response**:

```json
{
  "message": "주식 매수/매도 완료: Apple 5주"
}
```

---

### 3 Stock API

---

3.1 주식 목록 조회

- **URL**: GET /api/stocks
- **Respose**:

```json
[
    {
        "stockId": "STOCK001",
        "stockName": "Apple",
        "stockPrice": 183
    },
    {
        "stockId": "STOCK002",
        "stockName": "Google",
        "stockPrice": 253
    },
    ...
]
```

3.2 주식 기록 조회

- **URL**: GET /api/stocks/{stockName}/history
- **Respose**:

```json
[
    {
        "id": 1,
        "stockName": "Apple",
        "price": 95,
        "date": "2025-03-31T10:34:44.975655"
    },
    {
        "id": 33,
        "stockName": "Apple",
        "price": 101,
        "date": "2025-03-31T10:35:14.974556"
    },
    {
        "id": 65,
        "stockName": "Apple",
        "price": 91,
        "date": "2025-03-31T10:35:44.973586"
    },
...
]
```

# 구현 방식

**1. Spring Boot 기반의 계층적 아키텍처**

- Controller, Service, Repository 계층을 분리하여 유지보수성을 향상시켰다.
- @Service와 @Repository 어노테이션을 활용하여 Spring의 의존성 주입(DI)을 적용하였다.

**2. JPA와 Lombok을 활용한 엔티티 구성**

- @Entity, @Table을 사용하여 데이터베이스 테이블과 매핑하였다.
- @Id, @GeneratedValue(strategy = GenerationType.IDENTITY)을 활용하여 자동 증가 ID를 설정하였다.
- Lombok의 @Getter, @Setter, @NoArgsConstructor를 사용하여 보일러플레이트 코드를 최소화하였다.

**3. DTO를 활용한 데이터 구조화**

- LoginRequest, RegisterRequest 등의 DTO를 활용하여 요청(Request)과 응답(Response) 데이터를 분리하였다.

**4. 예외 처리 및 유효성 검증**

- IllegalArgumentException을 활용하여 잘못된 입력 데이터가 저장되지 않도록 방지하였다.
- AuthenticationException을 활용하려다 다른 방식으로 변경하였다.

**5. Spring Data JPA 기반의 Repository 패턴**

- JpaRepository를 상속하여 데이터베이스 접근 로직을 단순화하였다.
- findById, save 등의 기본 메서드를 활용하여 CRUD를 구현하였다.

**6. MariaDB 연동**

- application.properties 파일을 활용하여 MariaDB와의 연결을 설정하였다.

**7. Maven 기반 프로젝트 관리**

- pom.xml 파일을 통해 프로젝트 의존성을 관리하였다.

# 실습 내용

해당 실습은 Java 파일로만 구성된 애플리케이션에서 Spring Boot 프로젝트로 리팩토링하는 과정이었습니다.

### 1. 기존 Java 애플리케이션의 구조

---

**1.1 App 클래스**

- **역할**: 애플리케이션의 진입점으로, 로그인과 회원가입 기능을 제공.
- **특징**:
    - Scanner를 사용하여 콘솔에서 사용자 입력을 받음.
    - System.out.println으로 출력.
    - UserRepository, PlayerRepository, StockRepository 등을 직접 인스턴스화하여 데이터 관리.
    - 로그인 성공 시 SkalaStockMarket 클래스를 호출하여 주식 거래 기능 실행.

---

**1.2 SkalaStockMarket 클래스**

- **역할**: 주식 거래 기능(주식 목록 조회, 매수, 매도, 포트폴리오 조회)과 관리자 기능(주식 추가) 제공.
- **특징**:
    - Scanner를 활용한 사용자 입력 방식
    - System.out.println을 통한 콘솔 출력
    - Timer와 TimerTask를 사용하여 주기적으로 주가 변동 시뮬레이션.

---

**1.3 데이터 관리**

- CSV 파일을 사용하여 주식 데이터를 관리.
- PlayerRepository, StockRepository 등을 활용하여 메모리 내에서 데이터 저장 후, CSV로 내보내는 방식.
- **JPA 또는 관계형 데이터베이스 연동이 없는 구조.**

---

### 2. Spring Boot로 전환한 주요 변경 사항

---

**2.1 콘솔 기반 → 웹 기반**

- **변경 전**:
    - Scanner와 System.out.println을 사용하여 콘솔에서 사용자 입력을 받고 출력
- **변경 후**:
    - **Spring MVC**를 사용하여 REST API를 구현(StockController, PlayerController, UserController)
    - 프론트엔드(Vue 프로젝트)를 추가하여 웹 인터페이스 제공
    - AJAX 요청(fetch)을 통해 백엔드 API와 통신
- 주요 메서드 :
    - `@RestController`로 API 엔드포인트 정의
    - `@GetMapping`, `@PostMapping`으로 요청 처리

---

**2.2 csv 데이터 → MariaDB 데이터베이스**

- **변경 전**:
    - csv 파일에 모든 데이터 저장
- **변경 후**:
    - **Spring Data JPA**를 사용하여 MariaDB 데이터베이스와 연결
    - Stock, Player, User, PlayerStock 엔티티를 정의하고, JPA를 통해 데이터베이스에 저장
    - JpaReository 사용하여 CRUD 메서드 처리
- 주요 메서드 :
    - `findById()`, `save()`, `findAll()` 등

---

**2.3 Timer → Spring 스케줄링**

- **변경 전**:
    - SkalaStockMarket 클래스에서 Timer와 TimerTask를 사용하여 주기적으로 주가 변동 시뮬레이션(simulateMarketFluctuation)
- **변경 후**:
    - **Spring Scheduling**을 사용하여 `@Scheduled` 어노테이션으로 주기적 작업 구현
    - StockService에 추가: `@Scheduled(fixedRate = 30000)`
    - StockmarketApplication 추가: `@SpringBootApplication`,`@EnableScheduling`

---

**2.4 단일 클래스 → 계층 구조**

- **변경 전**:
    - SkalaStockMarket 클래스에 모든 로직(주식 조회, 매수, 매도, 관리자 기능)이 집중
- **변경 후**: **계층 구조**로 분리:
    - **Controller**: StockController, PlayerController, UserController, FrontendController
    - **Service**: StockService, PlayerService, UserService
        - PlayerServiceImpl, UserServiceImpl
    - **Repository**: StockRepository, PlayerRepository, UserRepository
    - **dto** : CommonResponse, Login/Register Request, StockRequest, UserResponse

---

**2.5 프론트엔드 개선**

- **변경 전**:
    - 콘솔 출력만 제공.
- **변경 후**:
    - Vue 프로젝트 빌드 후 resources/static 폴더에 프론트엔드 파일을 배포하여 웹 인터페이스 제공.
    - JavaScript와 AJAX(fetch API)를 사용하여 백엔드 API와 통신.
    - Login/Register를 처리하는 StartMain컴포넌트와, 주식 거래 시장인 StockMain 컴포넌트로 구성
    - StockMain은 StockList, PlayerStocks, StockGraph 총 3개의 컴포넌트로 이루어진 SPA 구현

---

2.6 기능 추가

- 주식 목록을 스크롤 형태로 제공
- 주식 가격 history를 chart로 제공
- 보유 자산을 현금, 보유 주식으로 나타낸  pie chart 제공
- 보유 주식 선택을 통해 빠른 매수, 매도 가능

### 트러블 슈팅

1. 파이차트 안그려짐 
    
    → 초기화 빈 배열이라서 렌더링을 조건부로, Chart Data가 설정된 뒤에 뜨도록 함
    
2. row-click 안먹힘 
    
    → ItemsTable.vue를 수정해 부모 컴포넌트로 데이터 넘길 수 있게 emit 설정
    
3. 플레이어의 totalAssets를 매번 구해야함
    
    → 정보를 불러올 때, PortfolioResponse dto에 totalAssets
    
4. 갱신 누르지 않으면 주가 변동이 적용이 안됨
    
    → 웹소켓 써서 해결해야 할듯? 나중에..
    

느낀점 

초반에 엔티티 설계가 중요하다

- 하다보니까 이것도 하고 저것도 해야해서 자꾸 늘어나는데 수정하는게 더 귀찮음

콘솔 로그 찍는걸 습관화 하자(프론트)

- ㅇㅇ

변수 이름 잘 통일하고 에러나면 잘 들여다보자 (백↔프론트)

- 자꾸 이름 매칭 안되게 하니까 로직은 맞게짜도 오류가 떠서 화가남

깃허브로 버전 관리하자

- 하나 해결하고 다른거 해결하려하니까 다시 처음게 안됨. 근데 못돌아감 ㅋㅋ
