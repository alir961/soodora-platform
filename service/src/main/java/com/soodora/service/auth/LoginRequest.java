package com.soodora.service.auth;

import com.soodora.domain.auth.LoginCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "Email of the account", example = "user@example.com", defaultValue = "user@example.com") String email,
        @Schema(description = "Password of the account", example = "passwordPassword#!9", defaultValue = "passwordPassword#!9") String password)
        implements
            Request<LoginCommand> {

    @Override
    public LoginCommand toCommand() throws InvalidRequestException {
        if (email == null || email.trim().isBlank())
            throw new InvalidRequestException("Missing email");
        if (password == null || password.isBlank())
            throw new InvalidRequestException("Missing password");

        return new LoginCommand(email.trim(), password);
    }

}
