package com.grooveconductor.application.auth;

import com.grooveconductor.application.common.Request;
import com.grooveconductor.domain.auth.LoginCommand;
import com.grooveconductor.application.common.InvalidRequestException;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;

public record LoginRequest(String email, String password) implements Request<LoginCommand> {

    @Override
    public LoginCommand toCommand() throws InvalidRequestException {
        if (email == null || email.trim().isBlank()) throw new InvalidRequestException("Missing email");
        if (password == null || password.isBlank()) throw new InvalidRequestException("Missing password");

        return new LoginCommand(createFromEmail(email.trim()), password);
    }

}
