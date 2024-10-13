package com.grooveconductor.domain.auth;

import com.grooveconductor.domain.user.Identifier;

public record LoginCommand(Identifier email, String password) {

}
