package com.soodora.infrastructure.oauth;

import com.soodora.domain.oauth.AuthorizationMapper;
import com.soodora.domain.oauth.RefreshableAuthorization;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class AuthorizationMapperImpl implements AuthorizationMapper {

    private final Map<String, RefreshableAuthorization> registerAuthorizations = new HashMap<>();

    @Override
    public String registerAuthorization(RefreshableAuthorization authorization) {
        Random random = new Random();
        String randomToken = String.valueOf(random.nextLong());

        while (registerAuthorizations.containsKey(randomToken))
            randomToken = String.valueOf(random.nextLong());
        registerAuthorizations.put(randomToken, authorization);
        return randomToken;
    }

    @Override
    public RefreshableAuthorization retrieveAuthorization(String external) {
        return registerAuthorizations.get(external);
    }

}
