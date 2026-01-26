package com.soodora.domain.common;

public class StringValidator {

    public static boolean isNotNullOrBlank(String input) {
        return input != null && !input.isBlank();
    }

}
