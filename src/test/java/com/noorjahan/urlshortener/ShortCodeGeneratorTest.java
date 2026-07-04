package com.noorjahan.urlshortener.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    private static final int EXPECTED_LENGTH = 6;
    private static final String VALID_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Test
    void testGenerate_Length() {
        String shortCode = ShortCodeGenerator.generate();
        assertEquals(EXPECTED_LENGTH, shortCode.length(), "Short code should be 6 characters");
    }

    @Test
    void testGenerate_ContainsValidCharacters() {
        String shortCode = ShortCodeGenerator.generate();
        for (char c : shortCode.toCharArray()) {
            assertTrue(VALID_CHARS.indexOf(c) >= 0, "Character " + c + " is not in valid charset");
        }
    }

    @RepeatedTest(100)
    void testGenerate_Uniqueness() {
        String code1 = ShortCodeGenerator.generate();
        String code2 = ShortCodeGenerator.generate();
        assertNotEquals(code1, code2, "Generated codes should be different (high probability)");
    }

    @Test
    void testGenerate_NotNull() {
        String shortCode = ShortCodeGenerator.generate();
        assertNotNull(shortCode, "Generated short code should not be null");
    }

    @Test
    void testGenerate_NotEmpty() {
        String shortCode = ShortCodeGenerator.generate();
        assertFalse(shortCode.isEmpty(), "Generated short code should not be empty");
    }

    @RepeatedTest(50)
    void testGenerate_NoSpecialCharacters() {
        String shortCode = ShortCodeGenerator.generate();
        assertFalse(shortCode.matches(".*[^a-zA-Z0-9].*"), 
                "Short code should not contain special characters");
    }
}
