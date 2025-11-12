package com.soodora.domain.auth;

import com.soodora.domain.user.User;

import java.util.Map;

public interface RegisterConfirmationRepository {

    void registerConfirmation(String secret, User user, String external);

    boolean hasConfirmation(String secret);

    Map.Entry<User, String> popConfirmation(String secret);

}
