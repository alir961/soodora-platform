package com.grooveconductor.domain.command;

import com.grooveconductor.domain.dto.Identifier;

public record LoginCommand(Identifier email, String password) {

}
