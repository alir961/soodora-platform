package com.grooveconductor.domain.auth;

import com.grooveconductor.domain.user.UserDTO;

public interface TokenRepository {

    String getSubjectFromToken(String token);

    String getSubjectFromGoogleToken(String token);

    String generateToken(UserDTO user);

}
