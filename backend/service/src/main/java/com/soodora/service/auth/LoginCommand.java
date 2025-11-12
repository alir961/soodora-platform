package com.soodora.service.auth;

import com.soodora.domain.user.value.Identifier;

public record LoginCommand(Identifier identifier, String password) {

}
