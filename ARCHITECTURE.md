# Payment Gateway Simulator - Architecture Documentation

## Overview

This document provides a comprehensive architectural description of the Payment Gateway Simulator, including system diagrams, component interactions, and data flow visualizations.

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                          CLIENT LAYER                                │
│  ┌──────────────────┐      ┌──────────────────┐                    │
│  │  Web Browser     │      │  API Client      │                    │
│  │  (Demo UI)       │      │  (REST/cURL)     │                    │
│  └────────┬─────────┘      └────────┬─────────┘                    │
└───────────┼────────────────────────┼──────────────────────────────┘
            │                        │
            ▼                        ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       CONTROLLER LAYER                               │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐ │
│  │ DemoController   │  │PaymentController │  │ScenarioController│ │
│  │   /demo/*        │  │  /api/payments   │  │  /api/scenarios  │ │
│  └────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘ │
│           │                     │                      │            │
│  ┌──────────────────┐  ┌──────────────────────────────┘            │
│  │ HealthController │  │                                            │
│  │   /api/health    │  │                                            │
│  └──────────────────┘  │                                            │
└────────────────────────┼────────────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        SERVICE LAYER                                 │
│  ┌──────────────────────────────┐  ┌──────────────────────────────┐│
│  │      PaymentService          │  │     ScenarioService          ││
│  │  - processPayment()          │  │  - createScenario()          ││
│  │  - getTransaction()          │  │  - getAllScenarios()         ││
│  │  - createCardFingerprint()   │  │  - deleteScenario()          ││
│  └──────────┬───────────────────┘  └──────────┬───────────────────┘│
└─────────────┼───────────────────────────────┼──────────────────────┘
              │                               │
              ▼                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      REPOSITORY LAYER                                │
│  ┌──────────────────────────────┐  ┌──────────────────────────────┐│
│  │  TransactionRepository       │  │   ScenarioRepository         ││
│  │  (In-Memory Storage)         │  │   (In-Memory Storage)        ││
│  │  - save()                    │  │   - save()                   ││
│  │  - findById()                │  │   - findMatchingScenario()   ││
│  │  - findAll()                 │  │   - findAll()                ││
│  └──────────────────────────────┘  └──────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────┘
              │                               │
              ▼                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         DATA MODELS                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐ │
│  │ Transaction  │  │   Scenario   │  │  TransactionStatus (Enum)│ │
│  │ - id         │  │  - name      │  │  - APPROVED              │ │
│  │ - merchantId │  │  - cardPat.. │  │  - DECLINED              │ │
│  │ - amount     │  │  - action    │  │  - FRAUD_SUSPECTED       │ │
│  │ - status     │  │  - delayMs   │  │  - TIMEOUT               │ │
│  └──────────────┘  └──────────────┘  │  - THREE_DS_REQUIRED     │ │
│                                       │  - ERROR                 │ │
│                                       └──────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

## Payment Processing Flow

```
┌──────────┐
│  Client  │
└────┬─────┘
     │
     │ 1. POST /api/payments
     │    {cardNumber, amount, ...}
     ▼
┌─────────────────────┐
│ PaymentController   │
│ - Validates request │
└────┬────────────────┘
     │
     │ 2. processPayment(request)
     ▼
┌─────────────────────────────────────────────┐
│ PaymentService                              │
│ ┌─────────────────────────────────────────┐ │
│ │ 1. Create Transaction object            │ │
│ │ 2. Hash card number → fingerprint       │ │
│ │ 3. Query ScenarioRepository             │ │
│ │    - Match by cardNumber pattern        │ │
│ │    - Match by merchantId                │ │
│ └─────────────────────────────────────────┘ │
└────┬────────────────────────────────────────┘
     │
     │ 3. findMatchingScenario(cardNumber, merchantId)
     ▼
┌─────────────────────┐
│ ScenarioRepository  │
│ - Returns Scenario  │
│   or empty          │
└────┬────────────────┘
     │
     │ 4. Scenario result
     ▼
┌─────────────────────────────────────────────┐
│ PaymentService (continued)                  │
│ ┌─────────────────────────────────────────┐ │
│ │ If scenario found:                      │ │
│ │   - Apply delay (Thread.sleep)          │ │
│ │   - Set status from scenario.action     │ │
│ │   - Set response code                   │ │
│ │   - Generate appropriate message        │ │
│ │ Else:                                   │ │
│ │   - Default to APPROVED                 │ │
│ └─────────────────────────────────────────┘ │
└────┬────────────────────────────────────────┘
     │
     │ 5. save(transaction)
     ▼
┌─────────────────────┐
│TransactionRepository│
│ - Stores in memory  │
└────┬────────────────┘
     │
     │ 6. PaymentResponse
     ▼
┌─────────────────────┐
│ PaymentController   │
│ - Returns response  │
│ - Sets HTTP status  │
└────┬────────────────┘
     │
     │ 7. HTTP Response
     │    {transactionId, status, message}
     ▼
┌──────────┐
│  Client  │
└──────────┘
```

## Component Interactions

### Controllers

1. **PaymentController** (`/api/payments`)
   - **POST** `/api/payments` - Process a payment
   - **GET** `/api/payments/{transactionId}` - Retrieve transaction details
   - Validates incoming requests using `@Valid`
   - Returns appropriate HTTP status codes based on transaction status

2. **ScenarioController** (`/api/scenarios`)
   - **GET** `/api/scenarios` - List all configured scenarios
   - **POST** `/api/scenarios` - Create new scenario (requires API key)
   - **DELETE** `/api/scenarios/{name}` - Delete a scenario
   - Protected by API key authentication for admin operations

3. **DemoController** (`/demo`)
   - **GET** `/demo/payment` - Serve payment form UI
   - Uses Thymeleaf templates for rendering

4. **HealthController** (`/api/health`)
   - **GET** `/api/health` - System health check
   - Returns uptime and status information

### Services

#### PaymentService
- **Core responsibilities:**
  - Process payment requests
  - Match card numbers to scenarios
  - Apply scenario rules (delays, statuses)
  - Create secure card fingerprints (SHA-256 hashing)
  - Manage transaction lifecycle

- **Key methods:**
  - `processPayment(PaymentRequest)` → `PaymentResponse`
  - `getTransaction(String)` → `Optional<Transaction>`
  - `createCardFingerprint(String)` → `String`

#### ScenarioService
- **Core responsibilities:**
  - Manage payment scenarios
  - Validate scenario configurations
  - Initialize default scenarios

- **Key methods:**
  - `createScenario(ScenarioRequest)` → `ScenarioResponse`
  - `getAllScenarios()` → `List<ScenarioResponse>`
  - `deleteScenario(String)` → `void`

### Repositories

Both repositories use **in-memory storage** with `ConcurrentHashMap` for thread-safe operations:

- **TransactionRepository**: Stores transaction records
- **ScenarioRepository**: Stores scenario configurations with pattern matching

## Test Card Numbers and Scenarios

```
Card Number         │ Pattern          │ Status           │ Response
───────────────────────────────────────────────────────────────────────
4242424242424242   │ 4242********     │ APPROVED         │ Success
4000000000000002   │ 4000********0002 │ DECLINED         │ insufficient_funds
4000000000000069   │ 4000********0069 │ FRAUD_SUSPECTED  │ fraud_risk_high
4000000000000077   │ 4000********0077 │ TIMEOUT          │ timeout (5s delay)
4000000000003220   │ 4000********3220 │ THREE_DS_REQUIRED│ 3ds_required
4000000000000085   │ 4000********0085 │ ERROR            │ processing_error
4000000000009999   │ 4000********9999 │ APPROVED         │ Success (3s delay)
```

## Security Features

### 1. Card Number Protection
```
Input: 4242424242424242
      ↓ SHA-256 Hash
Output: e3b0c44298fc1c1... (fingerprint - first 16 chars)
```

**Never stored in plain text!**

### 2. API Key Authentication
- Admin endpoints protected by `X-API-Key` header
- Configured via environment variable or application.properties
- Default key for development: `admin-secret-key`

### 3. Input Validation
- DTOs annotated with Jakarta Validation constraints
- `@NotNull`, `@NotBlank`, `@Pattern`, `@Positive`
- Global exception handler for validation errors

## Data Models

### Transaction
```java
class Transaction {
    String transactionId;        // UUID
    String merchantId;           // Merchant identifier
    BigDecimal amount;           // Payment amount
    String currency;             // e.g., "EUR", "USD"
    String cardFingerprint;      // SHA-256 hash
    TransactionStatus status;    // Enum: APPROVED, DECLINED, etc.
    String declineCode;          // Reason code for declines
    Instant createdAt;           // Transaction timestamp
    Map<String, String> metadata; // Additional data
}
```

### Scenario
```java
class Scenario {
    String name;                 // Scenario identifier
    String cardNumberPattern;    // e.g., "4242********"
    String merchantIdPattern;    // Optional merchant filter
    String action;               // Status to return
    String responseCode;         // Response/decline code
    Integer delayMs;             // Artificial delay
    String description;          // Human-readable description
}
```

## HTTP Status Code Mapping

```
Transaction Status  │ HTTP Status Code │ Description
─────────────────────────────────────────────────────────────
APPROVED           │ 200 OK           │ Payment successful
DECLINED           │ 402 Payment Req. │ Payment declined
FRAUD_SUSPECTED    │ 402 Payment Req. │ Fraud check failed
THREE_DS_REQUIRED  │ 200 OK           │ Additional auth needed
TIMEOUT            │ 500 Internal     │ Processing timeout
ERROR              │ 500 Internal     │ System error
```

## Technology Stack

### Backend Framework
- **Spring Boot 3.2.0** - Application framework
- **Java 17** - Programming language
- **Maven** - Build tool and dependency management

### Web Layer
- **Spring MVC** - REST API controllers
- **Thymeleaf** - Server-side templating
- **Jakarta Validation** - Request validation

### Testing
- **JUnit 5** - Unit testing framework
- **MockMvc** - Integration testing
- **RestAssured** - API testing
- **Selenium WebDriver** - E2E testing
- **Cucumber** - BDD testing

### DevOps
- **GitHub Actions** - CI/CD pipeline
- **WebDriverManager** - Browser driver management

## Deployment Architecture

```
┌────────────────────────────────────────────────────────────┐
│                      GitHub Actions                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │  CI Pipeline │  │ E2E Pipeline │  │ BDD Pipeline │    │
│  │  - Build     │  │  - Start app │  │  - Start app │    │
│  │  - Unit test │  │  - Selenium  │  │  - Cucumber  │    │
│  │  - API test  │  │  - Headless  │  │  - Headless  │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
└────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌────────────────────────────────────────────────────────────┐
│              Spring Boot Application                        │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  Embedded Tomcat Server (Port 8080)                  │ │
│  │  ┌──────────────┐  ┌──────────────────────────────┐ │ │
│  │  │ REST API     │  │ Static Resources             │ │ │
│  │  │ /api/*       │  │ HTML/CSS/JS                  │ │ │
│  │  └──────────────┘  └──────────────────────────────┘ │ │
│  └──────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  In-Memory Data Storage                              │ │
│  │  - ConcurrentHashMap for transactions               │ │
│  │  - ConcurrentHashMap for scenarios                  │ │
│  └──────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────┘
```

## Configuration

### Application Properties
```properties
# Server configuration
server.port=8080

# Logging
logging.level.com.pfa.paymentgateway=DEBUG

# API Security
api.admin.key=admin-secret-key  # Override with env var API_ADMIN_KEY
```

### Environment Variables
- `API_ADMIN_KEY` - Admin API key for scenario management
- `selenium.headless` - Run Selenium tests headless (true/false)
- `app.baseUrl` - Base URL for E2E tests (default: http://localhost:8080)

## Project Structure

```
Payment-Gateway-Simulator/
├── src/
│   ├── main/
│   │   ├── java/com/pfa/paymentgateway/
│   │   │   ├── PaymentGatewaySimulatorApplication.java  # Main entry point
│   │   │   ├── config/
│   │   │   │   └── GlobalExceptionHandler.java          # Exception handling
│   │   │   ├── controller/
│   │   │   │   ├── DemoController.java                  # Demo UI endpoints
│   │   │   │   ├── HealthController.java                # Health checks
│   │   │   │   ├── PaymentController.java               # Payment API
│   │   │   │   └── ScenarioController.java              # Scenario management
│   │   │   ├── dto/
│   │   │   │   ├── PaymentRequest.java                  # Payment input
│   │   │   │   ├── PaymentResponse.java                 # Payment output
│   │   │   │   ├── ScenarioRequest.java                 # Scenario input
│   │   │   │   └── ScenarioResponse.java                # Scenario output
│   │   │   ├── model/
│   │   │   │   ├── Scenario.java                        # Scenario entity
│   │   │   │   ├── Transaction.java                     # Transaction entity
│   │   │   │   └── TransactionStatus.java               # Status enum
│   │   │   ├── repository/
│   │   │   │   ├── ScenarioRepository.java              # Scenario storage
│   │   │   │   └── TransactionRepository.java           # Transaction storage
│   │   │   └── service/
│   │   │       ├── PaymentService.java                  # Payment logic
│   │   │       └── ScenarioService.java                 # Scenario logic
│   │   └── resources/
│   │       ├── application.properties                    # Main config
│   │       ├── application-test.properties               # Test config
│   │       ├── static/                                   # CSS/JS assets
│   │       └── templates/                                # Thymeleaf templates
│   └── test/
│       └── java/com/pfa/paymentgateway/
│           ├── unit/                                     # Unit tests
│           ├── integration/                              # Integration tests
│           ├── api/                                      # API tests
│           ├── e2e/                                      # E2E Selenium tests
│           └── bdd/                                      # BDD Cucumber tests
├── automation/
│   └── selenium/
│       └── resources/                                    # Test fixtures
├── .github/
│   └── workflows/
│       ├── ci.yml                                        # CI pipeline
│       ├── e2e.yml                                       # E2E pipeline
│       └── bdd.yml                                       # BDD pipeline
├── pom.xml                                               # Maven config
├── README.md                                             # User guide
├── RAPPORT.md                                            # Project report (French)
└── ARCHITECTURE.md                                       # This file
```

## Key Design Patterns

### 1. Layered Architecture
- Clear separation of concerns: Controllers → Services → Repositories
- Each layer has specific responsibilities
- Dependencies flow downward

### 2. Repository Pattern
- Abstract data access logic
- In-memory implementation (could be replaced with JPA/database)
- Thread-safe with ConcurrentHashMap

### 3. DTO Pattern
- Separate request/response objects from domain models
- Clean API contracts
- Validation at the boundary

### 4. Strategy Pattern (Scenarios)
- Different payment behaviors based on card number patterns
- Configurable at runtime
- Easy to add new scenarios

### 5. Dependency Injection
- Spring's @Autowired (constructor injection)
- Loose coupling between components
- Easy to test with mocks

## Extension Points

### Adding New Transaction Statuses
1. Add value to `TransactionStatus` enum
2. Update `PaymentService.applyScenario()` switch statement
3. Add HTTP status mapping in `PaymentController.getHttpStatus()`

### Adding Persistent Storage
1. Add Spring Data JPA dependency
2. Convert repositories to extend `JpaRepository`
3. Add database configuration to `application.properties`
4. Annotate models with JPA annotations

### Adding Authentication
1. Add Spring Security dependency
2. Configure `SecurityFilterChain`
3. Implement user authentication service
4. Add JWT token generation/validation

### Adding Webhooks
1. Create `WebhookService`
2. Add webhook configuration to `Scenario`
3. Implement async HTTP POST on transaction completion
4. Add retry logic and failure handling

## Performance Considerations

### Current Limitations
- **In-memory storage**: Lost on restart, limited capacity
- **Thread.sleep**: Blocks threads during delay simulation
- **No connection pooling**: Not needed for in-memory operations

### Scalability Improvements
- Replace in-memory storage with Redis or database
- Use CompletableFuture for async processing
- Add caching layer (e.g., Caffeine)
- Implement rate limiting

## Monitoring and Observability

### Available Endpoints
- `/api/health` - Application health status
- Spring Boot Actuator endpoints (if enabled):
  - `/actuator/health` - Detailed health info
  - `/actuator/metrics` - Application metrics

### Logging
- SLF4J with Logback
- Configurable log levels
- Key operations logged: payment processing, scenario matching

## Conclusion

This Payment Gateway Simulator provides a complete, production-like testing environment for payment integrations. The architecture is clean, extensible, and follows Spring Boot best practices. While designed for testing and demonstration, it showcases professional software engineering practices including layered architecture, comprehensive testing, CI/CD automation, and security considerations.

---

**For more information:**
- User Guide: See [README.md](README.md)
- Project Report: See [RAPPORT.md](RAPPORT.md)
- API Documentation: Start the application and visit `/demo/payment`
