package com.soodora.infrastructure.auth;

import com.soodora.domain.auth.RegisterConfirmationRepository;
import com.soodora.domain.user.User;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class RegisterConfirmationRepositoryImpl implements RegisterConfirmationRepository {

    private final Map<String, Map.Entry<User, String>> unconfirmedRegistrations = new HashMap<>();

    @Override
    public void registerConfirmation(String secret, User user, String external) {
        unconfirmedRegistrations.put(secret, new AbstractMap.SimpleEntry<>(user, external));
    }

    @Override
    public boolean hasConfirmation(String secret) {
        return unconfirmedRegistrations.containsKey(secret);
    }

    @Override
    public Map.Entry<User, String> popConfirmation(String secret) {
        Map.Entry<User, String> entry = unconfirmedRegistrations.get(secret);

        unconfirmedRegistrations.remove(secret);
        return entry;
    }

}
