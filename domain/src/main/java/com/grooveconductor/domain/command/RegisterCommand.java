package com.grooveconductor.domain.command;

import com.grooveconductor.domain.dto.Identifier;

public record RegisterCommand(String username, Identifier email, String password, String firstname, String lastname) {

}
