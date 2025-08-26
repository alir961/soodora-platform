package com.soodora.infrastructure.common;

import java.util.Random;

public class SlugGenerator {

    private static final int SLUG_LENGTH = 8;
    private static final String SLUG_CHARACTERS = "abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final Random random = new Random();

    public static String generateSlug() {
        StringBuilder builder = new StringBuilder();

        while (builder.length() < SLUG_LENGTH) {
            int index = (int) (random.nextFloat() * SLUG_CHARACTERS.length());

            builder.append(SLUG_CHARACTERS.charAt(index));
        }
        return builder.toString();
    }

}
