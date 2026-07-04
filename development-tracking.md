# Development Tracking - AI URL Shortener

**Project:** Noorjahan09/ai-url-shortener  
**Session Started:** 2026-07-05  
**Status:** In Progress

---

## Completed Features

### 1. Url Entity Class
**Purpose:** Core entity for storing shortened URL data

| Feature | Suggestion | Final Decision | Notes |
|---------|-----------|-----------------|-------|
| Primary Key | `@Id` with `@GeneratedValue(IDENTITY)` | ✅ Implemented | Long type, auto-increment |
| Original URL | Use `@NotBlank` validation \| Let Hibernate infer type | ✅ Moved validation to DTO | Entity has no validation, cleaner separation of concerns |
| Short Code | `@Column(unique=true)` + `@UniqueConstraint` | ✅ Implemented | Database-level enforcement of uniqueness |
| Short URL | Add as field \| **Remove (redundant)** | ❌ **REMOVED** | Can be computed from domain + shortCode |
| Timestamps | `createdAt` only \| **Add both** | ✅ Both implemented | `createdAt` (immutable) + `updatedAt` with `@PrePersist/@PreUpdate` |
| Expiration | Add `expiresAt` field | ⏳ **DEFERRED** | Added to backlog for future enhancement |
| Click Counter | Use Long type with default | ✅ Implemented | `@Builder.Default` with value 0L |
| Validations | Add in entity | ❌ **MOVED TO DTOs** | CreateShortURLRequest handles all input validation |
| Lombok Approach | `@Data` monolithic \| **Explicit annotations** | ✅ Explicit annotations | `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` |
| Table Name | `url` singular \| **`urls` plural** | ✅ Changed to `urls` | Follows REST convention and clarity |
| Column Types | Explicit `columnDefinition` \| **Let Hibernate decide** | ✅ Hibernate auto-mapping | Removed `columnDefinition`, cleaner configuration |

**Final Implementation:**
```java
@Entity
@Table(name = "urls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String originalUrl;
    
    @Column(nullable = false, unique = true)
    private String shortCode;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    @Builder.Default
    private Long clickCount = 0L;
}
```

**Database Schema Generated:**
```
urls table:
├── id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
├── originalUrl (VARCHAR, NOT NULL)
├── shortCode (VARCHAR, NOT NULL, UNIQUE)
├── createdAt (TIMESTAMP, NOT NULL, IMMUTABLE)
├── updatedAt (TIMESTAMP, NOT NULL, AUTO-UPDATE)
└── clickCount (BIGINT, NOT NULL, DEFAULT 0)
```

---

## Pending/Future Enhancements

- [ ] **Expiration Support** - Add `expiresAt` field for time-limited links
- [ ] **Link Metadata** - Add title, description, tags for management
- [ ] **Redirect Count Tracking** - More detailed analytics
- [ ] **Custom Short Codes** - Allow users to set vanity URLs
- [ ] **Bulk Operations** - Batch URL creation/deletion

---

## Architectural Decisions

### Validation Strategy
- **Entity:** Only structural constraints (`@Column(nullable=false)`, unique constraints)
- **DTOs:** All business validations (`@NotBlank`, `@URL`, etc.)
- **Reason:** Clean separation, easier to change rules without modifying database

### Timestamp Management
- **Auto-set on creation:** `@PrePersist` sets both `createdAt` and `updatedAt`
- **Auto-update on modification:** `@PreUpdate` refreshes `updatedAt`
- **Reason:** Ensures consistency, eliminates manual timestamp management

### Short URL Storage Decision
- **Initial:** Store computed `shortUrl` in database
- **Revised:** Compute on-the-fly from domain + shortCode
- **Reason:** Reduces data duplication, single source of truth for short code

### Lombok Annotation Choice
- **Alternative:** `@Data` (includes `@EqualsAndHashCode`, `@ToString`)
- **Selected:** Explicit annotations
- **Reason:** More control, avoids unnecessary method generation, clearer intent

---

## Technical Debt & Notes

None currently identified for Url entity.

---

## Next Steps

1. Create `UrlRepository` interface extending `JpaRepository`
2. Implement `UrlService` with URL shortening logic
3. Create `UrlController` with REST endpoints
4. Implement short code generation algorithm
5. Add unit tests for entity and service

---

*Last Updated: 2026-07-05T00:24:40+05:30*
