package com.soodora.domain.auth;

import com.soodora.domain.user.Identifier;

public record LoginCommand(Identifier identifier, String password) {

}
