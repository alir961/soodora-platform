package com.grooveconductor.domain.auth;

import com.grooveconductor.domain.user.UserDTO;

import java.util.Date;

public interface TokenRepository {

    String getEmailFromToken(String token);

    Date getExpirationDateFromToken(String token);

    String generateToken(UserDTO user);

}
