package com.grooveconductor.service.auth;

import com.grooveconductor.domain.auth.LoginCommand;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
    @Schema(example = "string@example.com")
    String email,
    @Schema(example = "stringString#")
    String password
) implements Request<LoginCommand> {

    @Override
    public LoginCommand toCommand() throws InvalidRequestException {
        if (email == null || email.trim().isBlank()) throw new InvalidRequestException("Missing email");
        if (password == null || password.isBlank()) throw new InvalidRequestException("Missing password");

        return new LoginCommand(email.trim(), password);
    }

}
