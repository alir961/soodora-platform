package com.soodora.service.auth;

import com.soodora.domain.auth.RegisterCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static java.lang.Character.*;

public record RegisterRequest(
        @Schema(description = "Username of the account", example = "Username", defaultValue = "Username") String username,
        @Schema(description = "Email of the account", example = "user@example.com", defaultValue = "user@example.com") String email,
        @Schema(description = "Password of the account", example = "passwordPassword#!9", defaultValue = "passwordPassword#!9") String password,
        @Schema(description = "Firstname of the account", example = "John", defaultValue = "John") String firstname,
        @Schema(description = "Lastname of account", example = "Doe", defaultValue = "Doe") String lastname,
        @Schema(description = "External OAuth 2.0 token") String external)
        implements
            Request<RegisterCommand> {

    @Override
    public RegisterCommand toCommand() throws InvalidRequestException {
        if (username == null || username.isBlank())
            throw new InvalidRequestException("Username is not valid");
        if (email == null || !email.contains("@"))
            throw new InvalidRequestException("Email is not valid");
        if (password == null || !isValidPassword(password))
            throw new InvalidRequestException("Password is not valid");

        return new RegisterCommand(
                username.trim(),
                email.trim(),
                password,
                firstname.trim(),
                lastname.trim(),
                external);
    }

    private boolean isValidPassword(String password) {
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        if (password.length() < 6)
            return false;
        for (char character : password.toCharArray()) {
            if (isLowerCase(character))
                hasLowerCase = true;
            else if (isUpperCase(character))
                hasUpperCase = true;
            else if (isDigit(character))
                hasDigit = true;
            else
                hasSpecial = true;

            if (hasLowerCase && hasUpperCase && hasDigit && hasSpecial)
                return true;
        }
        return false;
    }

}
