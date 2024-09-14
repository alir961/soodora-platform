package com.grooveconductor.request;

import com.grooveconductor.domain.command.LoginCommand;
import com.grooveconductor.exception.InvalidRequestException;

import static com.grooveconductor.domain.factory.IdentifierFactory.createFromEmail;

public record LoginRequest(String email, String password) implements Request<LoginCommand> {

    @Override
    public LoginCommand toCommand() {
        if (email == null || email.trim().isBlank()) throw new InvalidRequestException("Missing email");
        if (password == null || password.isBlank()) throw new InvalidRequestException("Missing password");

        return new LoginCommand(createFromEmail(email.trim()), password);
    }

}
