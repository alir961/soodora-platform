package com.soodora.service.auth;

import com.soodora.domain.auth.LoginCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.domain.user.IdentifierFactory.createFromEmail;
import static com.soodora.domain.user.IdentifierFactory.createFromUsername;

public record LoginRequest(
        @Schema(description = "Identifier of the account", example = "user@example.com", defaultValue = "user@example.com") String identifier,
        @Schema(description = "Password of the account", example = "passwordPassword#!9", defaultValue = "passwordPassword#!9") String password)
        implements
            Request<LoginCommand> {

    @Override
    public LoginCommand toCommand() throws InvalidRequestException {
        if (identifier == null || identifier.trim().isBlank())
            throw new InvalidRequestException("Missing identifier");
        if (password == null || password.isBlank())
            throw new InvalidRequestException("Missing password");

        return new LoginCommand(
                identifier.contains("@") ? createFromEmail(identifier.trim()) : createFromUsername(identifier.trim()),
                password);
    }

}
