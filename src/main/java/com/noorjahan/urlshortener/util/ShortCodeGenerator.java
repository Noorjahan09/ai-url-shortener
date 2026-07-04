package com.noorjahan.urlshortener.util;

import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@NoArgsConstructor
public class ShortCodeGenerator {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            shortCode.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return shortCode.toString();
    }
}
