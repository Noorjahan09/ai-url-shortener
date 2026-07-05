package com.noorjahan.urlshortener.repository;

import com.noorjahan.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * URL Repository - Data access layer for URL entity operations.
 *
 * <p>Extends Spring Data JPA's {@link JpaRepository} to provide CRUD and custom query methods
 * for URL entity persistence and retrieval.</p>
 *
 * @author Noorjahan09
 * @version 1.0
 */
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    /**
     * Finds a URL entity by its unique short code.
     *
     * @param shortCode the unique short code identifier
     * @return an {@link Optional} containing the URL if found, empty otherwise
     */
    Optional<Url> findByShortCode(String shortCode);

    /**
     * Checks if a short code already exists in the database.
     *
     * @param shortCode the short code to check
     * @return true if the short code exists, false otherwise
     */
    boolean existsByShortCode(String shortCode);

    /**
     * Finds a URL entity by its original URL.
     * Useful for duplicate detection when creating shortened URLs.
     *
     * @param originalUrl the original full URL
     * @return an {@link Optional} containing the URL if found, empty otherwise
     */
    Optional<Url> findByOriginalUrl(String originalUrl);
}
