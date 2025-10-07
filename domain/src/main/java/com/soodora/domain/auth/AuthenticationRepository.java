package com.soodora.domain.auth;

import com.soodora.domain.user.value.Identifier;

public interface AuthenticationRepository {

    String retrieveEncodedPassword(Identifier identifier);

}
