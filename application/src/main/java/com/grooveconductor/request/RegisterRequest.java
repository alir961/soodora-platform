package com.grooveconductor.request;

import com.grooveconductor.domain.command.RegisterCommand;
import com.grooveconductor.exception.InvalidRequestException;

import static com.grooveconductor.domain.factory.IdentifierFactory.createFromEmail;

public record RegisterRequest(String username, String email, String password, String firstname, String lastname) implements Request<RegisterCommand> {

    @Override
    public RegisterCommand toCommand() {
        if (username == null || username.trim().length() <= 5) throw new InvalidRequestException("Username is not valid");
        if (email == null || !email.contains("@")) throw new InvalidRequestException("Email is not valid");
        if (password == null || password.length() <= 5) throw new InvalidRequestException("Password is not valid");

        return new RegisterCommand(username.trim(), createFromEmail(email.trim()), password, firstname.trim(), lastname.trim());
    }

}
