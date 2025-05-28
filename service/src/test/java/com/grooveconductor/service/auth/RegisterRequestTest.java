package com.grooveconductor.service.auth;

import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.service.common.InvalidRequestException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterRequestTest {

    @Test
    void givenValidRequest_whenToCommand_thenSuccess() {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                " username",
                "   user@example.com ",
                "Password1234!",
                "John",
                "Doe",
                null);

        // WHEN
        RegisterCommand command = request.toCommand();

        // THEN
        assertEquals("username", command.username());
        assertEquals("user@example.com", command.email());
    }

    @Test
    void givenInvalidUsername_whenToCommand_thenFailure() {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                "   ",
                "user@example.com",
                "Password1234!",
                "John",
                "Doe",
                null);

        // WHEN
        Executable executable = request::toCommand;

        // THEN
        assertThrows(InvalidRequestException.class, executable, "Username is not valid");
    }

    @Test
    void givenInvalidEmail_whenToCommand_thenFailure() {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                "username",
                "invalid",
                "Password1234!",
                "John",
                "Doe",
                null);

        // WHEN
        Executable executable = request::toCommand;

        // THEN
        assertThrows(InvalidRequestException.class, executable, "Email is not valid");
    }

    @Test
    void givenPasswordTooShort_whenToCommand_thenFailure() {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                "username",
                "user@example.com",
                "Pas1!",
                "John",
                "Doe",
                null);

        // WHEN
        Executable executable = request::toCommand;

        // THEN
        assertThrows(InvalidRequestException.class, executable, "Password is not valid");
    }

    @Test
    void givenPasswordWithoutLowerCase_whenToCommand_thenFailure() {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                "username",
                "user@example.com",
                "PASSWORD1234!",
                "John",
                "Doe",
                null);

        // WHEN
        Executable executable = request::toCommand;

        // THEN
        assertThrows(InvalidRequestException.class, executable, "Password is not valid");
    }

    @Test
    void givenPasswordWithoutUpperCase_whenToCommand_thenFailure() {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                "username",
                "user@example.com",
                "password1234!",
                "John",
                "Doe",
                null);

        // WHEN
        Executable executable = request::toCommand;

        // THEN
        assertThrows(InvalidRequestException.class, executable, "Password is not valid");
    }

    @Test
    void givenPasswordWithoutDigit_whenToCommand_thenFailure() {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                "username",
                "user@example.com",
                "Password!",
                "John",
                "Doe",
                null);

        // WHEN
        Executable executable = request::toCommand;

        // THEN
        assertThrows(InvalidRequestException.class, executable, "Password is not valid");
    }

    @Test
    void givenPasswordWithoutSpecial_whenToCommand_thenFailure() {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                "username",
                "user@example.com",
                "Password1234",
                "John",
                "Doe",
                null);

        // WHEN
        Executable executable = request::toCommand;

        // THEN
        assertThrows(InvalidRequestException.class, executable, "Password is not valid");
    }

}
