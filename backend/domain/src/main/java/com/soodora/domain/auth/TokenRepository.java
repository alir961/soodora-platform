package com.soodora.domain.auth;

import com.soodora.domain.user.User;

public interface TokenRepository {

    String getSubjectFromToken(String token);

    String getSubjectFromGoogleToken(String token);

    String generateToken(User user);

}
