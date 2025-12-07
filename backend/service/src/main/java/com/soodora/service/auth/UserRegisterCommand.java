package com.soodora.service.auth;

public record UserRegisterCommand(
        String username,
        String email,
        String password,
        String firstname,
        String lastname,
        String external) {

}
