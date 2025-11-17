package com.soodora.domain.user.value;

import com.soodora.domain.common.InvalidValueException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    @Test
    void givenValidPassword_whenConstructPassword_thenConstructPassword() {
        // GIVEN
        String validPassword = "Password1234!";

        // WHEN
        Password password = new Password(validPassword);

        // THEN
        assertEquals(validPassword, password.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Pas1!", // too short
            "PASSWORD1234!", // no lowercase
            "password1234!", // no uppercase
            "Password!", // no digit
            "Password1234" // no special char
    })
    void givenInvalidPassword_whenConstructPassword_thenThrowsInvalidValueException(String invalidPassword) {
        // WHEN
        Executable executable = () -> new Password(invalidPassword);

        // THEN
        assertThrows(InvalidValueException.class, executable, "Password is not valid");
    }

}
