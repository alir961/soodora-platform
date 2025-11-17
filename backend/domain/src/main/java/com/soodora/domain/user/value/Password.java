package com.soodora.domain.user.value;

import com.soodora.domain.common.InvalidValueException;

import static java.lang.Character.isLetterOrDigit;

public record Password(String value) {

    public Password(String value) {
        if (!isValidPassword(value))
            throw new InvalidValueException("Password is not valid");
        this.value = value.trim();
    }

    private static boolean isValidPassword(String password) {
        if (password.length() < 6)
            return false;
        return password.chars().anyMatch(Character::isLowerCase)
                && password.chars().anyMatch(Character::isUpperCase)
                && password.chars().anyMatch(Character::isDigit)
                && password.chars().anyMatch(ch -> !isLetterOrDigit(ch));
    }

}
