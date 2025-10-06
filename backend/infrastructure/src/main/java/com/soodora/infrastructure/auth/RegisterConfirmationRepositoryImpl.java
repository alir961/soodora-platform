package com.soodora.infrastructure.auth;

import com.soodora.domain.auth.RegisterCommand;
import com.soodora.domain.auth.RegisterConfirmationRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class RegisterConfirmationRepositoryImpl implements RegisterConfirmationRepository {

    private final Map<String, RegisterCommand> unconfirmedRegistrations = new HashMap<>();

    @Override
    public void registerConfirmation(String secret, RegisterCommand command) {
        unconfirmedRegistrations.put(secret, command);
    }

    @Override
    public boolean hasConfirmation(String secret) {
        return unconfirmedRegistrations.containsKey(secret);
    }

    @Override
    public RegisterCommand popConfirmation(String secret) {
        RegisterCommand command = unconfirmedRegistrations.get(secret);

        unconfirmedRegistrations.remove(secret);
        return command;
    }

}
