package com.grooveconductor.service.auth;

import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;

public record RegisterRequest(
    String username,
    @Schema(example = "string@example.com")
    String email,
    @Schema(example = "stringString#")
    String password,
    String firstname,
    String lastname
) implements Request<RegisterCommand> {

    @Override
    public RegisterCommand toCommand() throws InvalidRequestException {
        if (username == null || username.trim().length() <= 5) throw new InvalidRequestException("Username is not valid");
        if (email == null || !email.contains("@")) throw new InvalidRequestException("Email is not valid");
        if (password == null || password.length() <= 5) throw new InvalidRequestException("Password is not valid");

        return new RegisterCommand(username.trim(), createFromEmail(email.trim()), password, firstname, lastname);
    }

}
