package com.grooveconductor.service.auth;

import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterRequest(
        @Schema(description = "Username of the account", example = "Username", defaultValue = "Username") String username,
        @Schema(description = "Email of the account", example = "user@example.com", defaultValue = "user@example.com") String email,
        @Schema(description = "Password of the account", example = "passwordPassword#!", defaultValue = "passwordPassword#!") String password,
        @Schema(description = "Firstname of the account", example = "John", defaultValue = "John") String firstname,
        @Schema(description = "Lastname of account", example = "Doe", defaultValue = "Doe") String lastname)
        implements
            Request<RegisterCommand> {

    @Override
    public RegisterCommand toCommand() throws InvalidRequestException {
        if (username == null || username.trim().length() <= 5)
            throw new InvalidRequestException("Username is not valid");
        if (email == null || !email.contains("@"))
            throw new InvalidRequestException("Email is not valid");
        if (password == null || password.length() <= 5)
            throw new InvalidRequestException("Password is not valid");

        return new RegisterCommand(
                username.trim(),
                email.trim(),
                password,
                firstname,
                lastname);
    }

}
