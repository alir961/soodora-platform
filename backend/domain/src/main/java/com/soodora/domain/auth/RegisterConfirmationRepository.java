package com.soodora.domain.auth;

public interface RegisterConfirmationRepository {

    void registerConfirmation(String secret, RegisterCommand command);

    boolean hasConfirmation(String secret);

    RegisterCommand popConfirmation(String secret);

}
