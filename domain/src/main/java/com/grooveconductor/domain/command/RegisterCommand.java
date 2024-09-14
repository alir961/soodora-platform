package com.grooveconductor.domain.command;

public record RegisterCommand(String username, String email, String password, String firstname, String lastname) {

}
