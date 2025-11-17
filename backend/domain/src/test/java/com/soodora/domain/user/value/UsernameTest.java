package com.soodora.domain.user.value;

import com.soodora.domain.common.InvalidValueException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class UsernameTest {

    @Test
    void givenValidUsername_whenConstructUsername_thenConstructUsername() {
        // GIVEN
        String validUsername = "username";

        // WHEN
        Username username = new Username(validUsername);

        // THEN
        assertEquals(validUsername, username.value());
    }

    @Test
    void givenBlankUsername_whenConstructUsername_thenThrowsInvalidValueException() {
        // GIVEN
        String invalidUsername = "  ";

        // WHEN
        Executable executable = () -> new Username(invalidUsername);

        // THEN
        assertThrows(InvalidValueException.class, executable, "Invalid username");
    }

    @Test
    void givenTooLongUsername_whenConstructUsername_thenThrowsInvalidValueException() {
        // GIVEN
        String invalidUsername = "usernameVeryTooLongToBeUsedAsItBecauseOfTheLongSuffix";

        // WHEN
        Executable executable = () -> new Username(invalidUsername);

        // THEN
        assertThrows(InvalidValueException.class, executable, "Invalid username");

    }

}
