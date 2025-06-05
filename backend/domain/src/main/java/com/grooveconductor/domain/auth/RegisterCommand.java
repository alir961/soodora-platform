package com.grooveconductor.domain.auth;

public record RegisterCommand(
        String username,
        String email,
        String password,
        String firstname,
        String lastname,
        String external) {

    public RegisterCommand updatePassword(String password) {
        return new RegisterCommand(username, email, password, firstname, lastname, external);
    }

}
