package com.grooveconductor.domain.auth;

import com.grooveconductor.domain.user.Identifier;

public record RegisterCommand(String username, Identifier email, String password, String firstname, String lastname) {

    public RegisterCommand updatePassword(String password) {
        return new RegisterCommand(username, email, password, firstname, lastname);
    }

}
