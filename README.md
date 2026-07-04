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

## License

This project is open source and available under the MIT License.

---

## Contributing

Contributions are welcome! Please follow these guidelines:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/improvement`)
3. Commit changes (`git commit -m 'Add improvement'`)
4. Push to branch (`git push origin feature/improvement`)
5. Open a Pull Request

---

**Last Updated:** 2026-07-05  
**Maintained by:** Noorjahan09
