package com.soodora.infrastructure.auth;

import com.soodora.domain.auth.AuthenticationRepository;
import com.soodora.domain.user.UserRepository;
import com.soodora.domain.user.value.Identifier;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuthenticationRepositoryImpl implements AuthenticationRepository {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public String retrieveEncodedPassword(Identifier identifier) {
        return userRepository.find(identifier)
                .getPassword();
    }

}
