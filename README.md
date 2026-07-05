# AI URL Shortener

A lightweight, efficient URL shortening service built with Spring Boot that generates unique short codes for long URLs and tracks access metrics.

---

## Project Overview

**AI URL Shortener** is a REST API service that:
- Converts long URLs into short, unique codes
- Stores URL mappings with audit timestamps
- Tracks click metrics and analytics
- Prevents duplicate URL shortening (idempotent)
- Provides structured error handling

**Key Features:**
- ✅ Generate unique 6-character short codes
- ✅ Retrieve original URLs from short codes
- ✅ Track click counts and access history
- ✅ Comprehensive error handling with structured responses
- ✅ Input validation for URL formats
- ✅ H2 in-memory database for quick setup

---

## Architecture

```
┌─────────────────┐
│   Controller    │ (UrlController)
│   /api/v1/urls  │
└────────┬────────┘
         │
┌────────▼────────┐
│    Service      │ (UrlServiceImpl)
│ Business Logic  │
└────────┬────────┘
         │
┌────────▼────────┐
│  Repository     │ (UrlRepository)
│  Data Access    │
└────────┬────────┘
         │
┌────────▼────────┐
│  Entity (Url)   │
│   Database      │
└─────────────────┘

Exception Handling:
┌──────────────────────────┐
│ GlobalExceptionHandler   │
│ - UrlNotFoundException   │
│ - Validation Errors      │
│ - Generic Exceptions     │
└──────────────────────────┘
         ↓
    ErrorResponse (DTO)
```

**Layers:**
- **Controller Layer:** REST endpoints, request/response handling
- **Service Layer:** Business logic, URL creation, analytics
- **Repository Layer:** Database operations (Spring Data JPA)
- **Entity Layer:** Url model with timestamps and metadata
- **Exception Layer:** Centralized error handling

---

## Technologies

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.5.16 |
| Build Tool | Maven | 3.x |
| Language | Java | 17 |
| Database | H2 (In-Memory) | Latest |
| ORM | Hibernate (JPA) | Latest |
| Validation | Jakarta Validation | Latest |
| Lombok | Code Generation | Latest |
| REST | Spring Web | Latest |

**Dependencies:**
- `spring-boot-starter-data-jpa` - Database operations
- `spring-boot-starter-web` - REST APIs
- `spring-boot-starter-validation` - Input validation
- `lombok` - Code generation (getters, setters, builders)
- `h2database` - In-memory database
- `spring-boot-starter-test` - Unit testing

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.6+
- Git

### Clone Repository
```bash
git clone https://github.com/Noorjahan09/ai-url-shortener.git
cd ai-url-shortener
```

### Build
```bash
mvn clean install
```

### Run Application
```bash
mvn spring-boot:run
```

**Output:**
```
Started UrlshortenerApplication in X.XXX seconds
Tomcat started on port(s): 8080
```

**Access the Application:**
- API Base URL: `http://localhost:8080/api/v1/urls`
- H2 Console: `http://localhost:8080/h2-console`

---

## API Endpoints

### 1. Create Short URL
**POST** `/api/v1/urls`

**Request:**
```json
{
  "orignalUrl": "https://www.example.com/very/long/url/path"
}
```

**Response (201 Created):**
```json
{
  "originalUrl": "https://www.example.com/very/long/url/path",
  "shortCode": "aB3cDe",
  "shortUrl": "http://localhost:8080/aB3cDe"
}
```

**Validation:**
- URL is required (not blank)
- Must start with `http://` or `https://`

---

### 2. Get Short URL
**GET** `/api/v1/urls/{shortCode}`

**Example:**
```
GET /api/v1/urls/aB3cDe
```

**Response (200 OK):**
```json
{
  "originalUrl": "https://www.example.com/very/long/url/path",
  "shortCode": "aB3cDe",
  "shortUrl": "http://localhost:8080/aB3cDe"
}
```

**Note:** Each request increments the click count.

---

### 3. Get Analytics
**GET** `/api/v1/urls/{shortCode}/analytics`

**Example:**
```
GET /api/v1/urls/aB3cDe/analytics
```

**Response (200 OK):**
```json
{
  "originalUrl": "https://www.example.com/very/long/url/path",
  "shortCode": "aB3cDe",
  "clickCount": 5,
  "createdAt": "2026-07-05T03:29:19"
}
```

---

## Error Responses

### 404 Not Found
```json
{
  "statusCode": 404,
  "message": "Short URL not found for code: xyz123",
  "error": "NOT_FOUND",
  "timestamp": "2026-07-05T03:29:19"
}
```

### 400 Bad Request (Validation)
```json
{
  "statusCode": 400,
  "message": "Validation failed",
  "error": "orignalUrl: Original URL must start with http:// or https://, orignalUrl: Original URL is required",
  "timestamp": "2026-07-05T03:29:19"
}
```

### 500 Internal Server Error
```json
{
  "statusCode": 500,
  "message": "Unexpected error occurred",
  "error": "INTERNAL_SERVER_ERROR",
  "timestamp": "2026-07-05T03:29:19"
}
```

---

## H2 Console

Access the H2 database console for debugging:

**URL:** `http://localhost:8080/h2-console`

**Configuration:**
- Driver Class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave blank)

**Useful Queries:**
```sql
SELECT * FROM urls;
SELECT * FROM urls WHERE short_code = 'aB3cDe';
SELECT * FROM urls ORDER BY created_at DESC LIMIT 10;
UPDATE urls SET click_count = click_count + 1 WHERE id = 1;
```

---

## Engineering Decisions

Engineering decisions represent strategic choices that balance trade-offs between functionality, performance, maintainability, and scalability.

### 1. **Duplicate URL Reuse**
- **Decision:** Return existing short code if URL already shortened
- **Trade-off:** Idempotency vs. fresh analytics per request
- **Reasoning:** Prevents URL bloat in database; aligns with REST idempotency principles
- **Example:** Two requests for `https://example.com` return the same short code
- **Future:** Can be changed to `create_if_not_exists` flag in request

### 2. **H2 In-Memory Database**
- **Decision:** Use H2 for development, easy to switch to PostgreSQL/MySQL
- **Trade-off:** Speed and simplicity vs. persistence and scale
- **Reasoning:** Perfect for MVP and local development; all data lost on restart (acceptable for prototype)
- **Migration Path:** Change `application.properties` datasource URL to PostgreSQL JDBC URL
- **Schema:** Automatically created via Hibernate with `spring.jpa.hibernate.ddl-auto=create-drop`

### 3. **Layered Architecture (4-Layer)**
- **Decision:** Controller → Service → Repository → Entity (+ Exception Handler)
- **Trade-off:** More files vs. clear separation of concerns
- **Reasoning:** Each layer has single responsibility; easier to test, maintain, and scale
- **Benefits:**
  - Controllers handle HTTP only (request/response)
  - Services contain business logic
  - Repositories isolate data access
  - Entities define schema only

### 4. **Global Exception Handling**
- **Decision:** Centralized `@RestControllerAdvice` for all exceptions
- **Trade-off:** Less verbose than try-catch blocks vs. less granular control
- **Reasoning:** Consistent error format; easier API contract; simpler client handling
- **Error Types:** 404 (Not Found), 400 (Validation), 500 (Internal Server Error)

### 5. **Immutable Creation Timestamps**
- **Decision:** `createdAt` field cannot be updated; `updatedAt` auto-refreshes
- **Trade-off:** Prevent accidental data manipulation vs. slight performance overhead
- **Reasoning:** Audit trail integrity; track actual creation time vs. modification
- **Implementation:** JPA `@PrePersist` and `@PreUpdate` callbacks

### 6. **DTO Pattern (No Entity Exposure)**
- **Decision:** APIs work exclusively with DTOs; entities never exposed
- **Trade-off:** More classes to maintain vs. API stability and flexibility
- **Reasoning:** Entity schema can evolve independently; prevents leaking internal structure
- **Benefit:** Can change entity columns without breaking client contracts

### 7. **Validation at DTO Layer**
- **Decision:** All input validation (`@NotBlank`, `@Pattern`) in request DTOs
- **Trade-off:** DTOs more complex vs. clean entity and service separation
- **Reasoning:** Validations are business rules, not database constraints
- **Pattern URL:** `^(https?://).*` enforces http:// or https:// protocol

### 8. **SecureRandom for Short Code Generation**
- **Decision:** Use cryptographically strong random (`SecureRandom`)
- **Trade-off:** Slightly slower than `Math.random()` vs. better randomness quality
- **Reasoning:** Prevents predictable patterns; important for security
- **Charset:** BASE62 (0-9a-zA-Z) for URL-safe codes without special characters

### 9. **6-Character Short Codes**
- **Decision:** Fixed length of 6 characters from BASE62 charset
- **Trade-off:** Fixed length vs. variable length based on volume
- **Calculation:** 62^6 = ~56 billion unique combinations
- **Collision Strategy:** Database uniqueness constraint + retry on collision
- **Future:** Can use variable length if collision rate becomes issue

### 10. **SLF4J Logging with @Slf4j**
- **Decision:** Use Lombok's `@Slf4j` annotation for logging
- **Trade-off:** Dependency on Lombok vs. clean code without boilerplate
- **Log Levels:**
  - DEBUG: Short code generation, method entry
  - INFO: URL created, retrieved, click count updated
  - WARN: URL not found, validation failures
  - ERROR: Unexpected exceptions

### 11. **Click Count Increment on Retrieval**
- **Decision:** Increment click count whenever `getShortUrl()` is called
- **Trade-off:** Extra database write per request vs. accurate click tracking
- **Future Optimization:** Could use periodic batch updates or separate analytics table
- **Current:** Simple load-modify-save pattern

### 12. **REST API Versioning**
- **Decision:** `/api/v1/urls` base path with version in URL
- **Trade-off:** Verbose URLs vs. clear version contracts
- **Future:** Can add v2, v3 endpoints for breaking changes
- **Benefits:** Multiple versions can coexist; clear deprecation path

### 13. **HTTP Status Codes**
- **Decision:** 201 CREATED for POST (create), 200 OK for GET, 404/400/500 for errors
- **Reasoning:** RESTful compliance; client-friendly status codes
- **Benefits:** Clients can handle responses based on status codes alone

### 14. **Lombok Annotations (Explicit over @Data)**
- **Decision:** Use `@Getter`, `@Setter`, `@Builder` instead of `@Data`
- **Trade-off:** More verbose vs. control over generated methods
- **Reasoning:** `@Data` includes `@EqualsAndHashCode` and `@ToString` which can cause issues in entities
- **Benefit:** No accidental infinite loops or memory leaks from toString()

### 15. **Optional-based Null Handling**
- **Decision:** Use Java `Optional` in repository methods
- **Trade-off:** More code vs. null safety
- **Reasoning:** Encourages explicit handling of "not found" cases; prevents NPE
- **Pattern:** `.orElseThrow(() -> new UrlNotFoundException(...))`

---

## Design Decisions


### 1. **DTO Pattern (No Entity Exposure)**
- **Decision:** Entities never exposed to clients/controllers
- **Reason:** Cleaner API contracts, easier to evolve schema
- **Implementation:** Separate DTOs for requests (CreateShortURLRequest) and responses (UrlResponse, AnalyticsResponse)

### 2. **Validation at DTO Layer**
- **Decision:** All input validation in DTOs, not entity
- **Reason:** Separation of concerns, reusable validation rules
- **Implementation:** `@NotBlank`, `@Pattern` on DTO fields

### 3. **Immutable Timestamps**
- **Decision:** `createdAt` is NOT updatable, `updatedAt` auto-refreshes
- **Reason:** Audit trail, prevent data manipulation
- **Implementation:** `@PrePersist` and `@PreUpdate` callbacks

### 4. **Short Code Generation**
- **Decision:** 6-character random codes using BASE62 charset
- **Reason:** Balance between uniqueness (~56B combinations) and URL length
- **Implementation:** SecureRandom for cryptographic strength, collision checking in DB

### 5. **Duplicate URL Handling**
- **Decision:** Return existing short code if URL already shortened
- **Reason:** Idempotent operation, reduces database bloat
- **Implementation:** Check `findByOriginalUrl()` before generating new code

### 6. **Click Tracking**
- **Decision:** Increment click count on each `getShortUrl()` call
- **Reason:** Simple metrics without separate analytics tables
- **Implementation:** Load-modify-save pattern (could be optimized with `@Query` update)

### 7. **Lombok for Code Generation**
- **Decision:** Explicit annotations (`@Getter`, `@Setter`, `@Builder`) over `@Data`
- **Reason:** Fine-grained control, avoid unwanted method generation
- **Implementation:** No `@EqualsAndHashCode`, `@ToString` clutter

### 8. **Error Handling**
- **Decision:** Custom ErrorResponse DTO for all errors
- **Reason:** Consistent error format, easier client-side handling
- **Implementation:** GlobalExceptionHandler with 3 handlers (NotFound, Validation, Generic)

---

## Future Improvements

- [ ] **Expiration Support** - Add `expiresAt` field for time-limited links
- [ ] **Custom Short Codes** - Allow users to create vanity URLs
- [ ] **Batch Operations** - Bulk create/delete URLs
- [ ] **Link Metadata** - Store title, description, tags
- [ ] **Redirect Endpoint** - `GET /r/{shortCode}` auto-redirects to original
- [ ] **QR Code Generation** - Generate QR codes for short URLs
- [ ] **Caching Layer** - Redis for hot URLs
- [ ] **Rate Limiting** - Prevent abuse
- [ ] **Authentication** - User-specific URL management
- [ ] **Advanced Analytics** - Referrer tracking, geographic data
- [ ] **Database Persistence** - Switch from H2 to PostgreSQL/MySQL
- [ ] **API Documentation** - Swagger/OpenAPI integration

---

## AI Usage

This project was developed with assistance from **GitHub Copilot CLI**, leveraging AI for:

✅ **Code Generation**
- Entity models with JPA annotations
- Service implementations with business logic
- REST controller boilerplate
- Exception handling patterns

✅ **Architecture Planning**
- Layered architecture design
- DTO pattern implementation
- Error handling strategy

✅ **Best Practices**
- Spring Boot conventions
- Lombok configuration
- Validation annotations
- Repository query methods

✅ **Documentation**
- API endpoint specifications
- Configuration examples
- Design rationale

**Key AI Contributions:**
- Suggested DTO-only exposure pattern
- Recommended immutable timestamp handling
- Optimized short code generation algorithm
- Structured error response design

---

## Project Structure

```
src/
├── main/
│   ├── java/com/noorjahan/urlshortener/
│   │   ├── controller/
│   │   │   └── UrlController.java
│   │   ├── service/
│   │   │   ├── IUrlService.java
│   │   │   └── impl/
│   │   │       └── UrlServiceImpl.java
│   │   ├── repository/
│   │   │   └── UrlRepository.java
│   │   ├── entity/
│   │   │   └── Url.java
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   └── CreateShortURLRequest.java
│   │   │   └── response/
│   │   │       ├── UrlResponse.java
│   │   │       └── AnalyticsResponse.java
│   │   ├── exception/
│   │   │   ├── UrlNotFoundException.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ErrorResponse.java
│   │   ├── util/
│   │   │   └── ShortCodeGenerator.java
│   │   └── UrlshortenerApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/...
```

---

## Scenarios: AI-Assisted Development Approaches

This section demonstrates how to handle three common development scenarios using AI assistants like GitHub Copilot CLI.

### Scenario 1: Greenfield Project (New from Scratch)

**Situation:** Building a URL shortener from zero, no existing codebase.

**Decomposition:**
```
Problem: "Build a URL shortener REST API"
  ├── Data Layer
  │   ├── Entity design (URL model)
  │   └── Repository queries
  ├── Business Logic
  │   ├── Service interface
  │   └── URL shortening algorithm
  ├── API Layer
  │   ├── REST endpoints
  │   └── Error handling
  ├── Utilities
  │   └── Short code generation
  └── Testing
      ├── Unit tests
      └── Integration tests
```

**Execution:**
1. Define entity with Javadocs
2. Create repository with query methods
3. Build service interface
4. Implement service with business logic
5. Create controller endpoints
6. Add global exception handler
7. Write comprehensive unit tests

**Validation:**
- ✅ All endpoints tested with MockMvc
- ✅ Service layer mocked in tests
- ✅ Exception handling verified
- ✅ Input validation working
- ✅ Database queries optimized

**AI Contributions:**
- Suggested entity structure with JPA annotations
- Recommended DTO pattern for API layer
- Generated service method contracts
- Created test templates with Mockito

---

### Scenario 2: Brownfield Project (Existing Codebase)

**Situation:** Adding URL shortening feature to existing platform with established patterns.

**Decomposition:**
```
Existing System: E-commerce platform
  └── New Feature: URL shortening for product links
      ├── Integrate with existing DB
      │   └── Follow established naming conventions
      ├── Align with existing API patterns
      │   └── Use same error response format
      ├── Follow authentication model
      │   └── Extend existing security config
      ├── Reuse existing utilities
      │   └── Use platform's ID generation
      └── Match existing test patterns
          └── Use same testing framework
```

**Execution:**
1. Analyze existing code structure and patterns
2. Create URL entity matching DB conventions
3. Implement service using existing patterns
4. Add controller endpoints following REST conventions
5. Use existing exception handler infrastructure
6. Write tests matching platform standards

**Validation:**
- ✅ Entity follows table naming conventions
- ✅ Error responses consistent with platform
- ✅ Service integrates with existing auth
- ✅ Tests use same framework/mocks
- ✅ No breaking changes to existing code

**AI Contributions:**
- Analyzed existing patterns and suggested compatible design
- Generated code following established conventions
- Identified reusable components (auth, error handling)
- Refactored code to align with platform standards

---

### Scenario 3: Ambiguous Requirements

**Situation:** Unclear or evolving requirements; "build something like Google's URL shortener."

**Decomposition:**
```
Vague Requirement: "URL shortener like Google"
  ├── Clarify Scope
  │   ├── Is expiration needed? (DEFERRED)
  │   ├── Analytics required? (YES - basic clicks)
  │   ├── Custom codes? (NO - auto-generate)
  │   ├── Multi-user? (NO - simple service)
  │   └── Scale? (H2 database for MVP)
  ├── Define MVP
  │   ├── Create: Generate short code
  │   ├── Retrieve: Get original URL
  │   └── Analytics: Track clicks
  ├── Plan Enhancements
  │   ├── QR codes
  │   ├── Expiration
  │   ├── Custom codes
  │   └── Advanced analytics
  └── Build Iteratively
      ├── Sprint 1: Core functionality
      ├── Sprint 2: Error handling + validation
      ├── Sprint 3: Analytics + testing
      └── Sprint 4+: Enhancement backlog
```

**Execution:**
1. **Define Scope with Stakeholders:**
   - What are core features?
   - What can wait for v2?
   - Performance expectations?

2. **Build MVP First:**
   - Basic entity with only essential fields
   - Service with 3 main operations
   - Simple in-memory database

3. **Add Validation & Error Handling:**
   - Input validation (HTTP/HTTPS only)
   - Proper exception responses
   - Database constraints

4. **Expand with Tests:**
   - Unit tests for each component
   - Happy path + error cases
   - Mocked dependencies

5. **Plan v2 Enhancements:**
   - Document in "Future Improvements"
   - Maintain backlog in development-tracking.md

**Validation:**
- ✅ MVP meets core requirements
- ✅ Extensible design for future features
- ✅ Clear separation of concerns
- ✅ Comprehensive testing
- ✅ Well-documented decisions

**AI Contributions:**
- Helped prioritize ambiguous requirements into MVP
- Suggested extensible architecture
- Identified future enhancement patterns
- Created development tracking system
- Generated flexible code for evolving needs

---

## Key Takeaways

| Aspect | Greenfield | Brownfield | Ambiguous |
|--------|-----------|-----------|----------|
| **Challenge** | Start from scratch | Respect existing patterns | Unclear goals |
| **AI Role** | Generate boilerplate, design | Analyze & align patterns | Prioritize & clarify |
| **Validation** | Test comprehensively | Match conventions | Iterate with stakeholders |
| **Outcome** | Clean new code | Integrated feature | MVP + backlog |

---

## Contributions are welcome! Please follow these guidelines:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/improvement`)
3. Commit changes (`git commit -m 'Add improvement'`)
4. Push to branch (`git push origin feature/improvement`)
5. Open a Pull Request

---

**Last Updated:** 2026-07-05  
**Maintained by:** Noorjahan09
