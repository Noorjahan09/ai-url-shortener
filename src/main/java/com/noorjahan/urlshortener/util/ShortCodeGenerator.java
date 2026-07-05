package com.noorjahan.urlshortener.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

/**
 * Short Code Generator - Utility for generating unique short codes.
 *
 * <p>Generates random 6-character alphanumeric codes using cryptographically
 * secure random number generation. Uses BASE62 charset (0-9a-zA-Z) for URL-safe codes.</p>
 *
 * @author Noorjahan09
 * @version 1.0
 */
@Slf4j
public class ShortCodeGenerator {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a unique 6-character short code.
     *
     * <p>Each character is randomly selected from BASE62 charset.
     * Probability of collision is extremely low due to 56+ billion combinations.</p>
     *
     * @return a randomly generated 6-character short code
     */
    public static String generate() {
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            shortCode.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        String code = shortCode.toString();
        log.debug("Generated short code: {}", code);
        return code;
    }
}

