package com.noorjahan.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * URL Entity - Represents a shortened URL mapping in the database.
 *
 * <p>This entity stores the original URL, unique short code, and access metrics.
 * It maintains audit trails with immutable creation timestamps and auto-updating
 * modification timestamps.</p>
 *
 * <p>Table: {@code urls}</p>
 *
 * @author Noorjahan09
 * @version 1.0
 */
@Entity
@Table(name = "urls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    /**
     * Unique identifier for the URL record. Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The original full URL being shortened. Cannot be null.
     */
    @Column(nullable = false)
    private String originalUrl;

    /**
     * Unique short code generated for this URL. Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true)
    private String shortCode;

    /**
     * Timestamp when the URL was created. Immutable after creation.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update. Auto-updated on every modification.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Number of times this short URL has been accessed. Defaults to 0.
     */
    @Column(nullable = false)
    @Builder.Default
    private Long clickCount = 0L;

    /**
     * JPA callback invoked before entity is persisted.
     * Initializes both {@code createdAt} and {@code updatedAt} to current time.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * JPA callback invoked before entity is updated.
     * Refreshes {@code updatedAt} to current time.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

