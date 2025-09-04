package com.soodora.domain.common;

import java.util.Random;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SlugGenerator {

    private static final int SLUG_LENGTH = 8;
    private static final String SLUG_CHARACTERS = "abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final Random random = new Random();

    public static String generateSlug() {
        return generateSlug(SLUG_LENGTH);
    }

    public static String generateSlug(int length) {
        StringBuilder builder = new StringBuilder();

        while (builder.length() < length) {
            int index = random.nextInt(SLUG_CHARACTERS.length());

            builder.append(SLUG_CHARACTERS.charAt(index));
        }
        return builder.toString();
    }

}
