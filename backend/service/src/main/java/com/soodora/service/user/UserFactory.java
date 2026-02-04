package com.soodora.service.user;

import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Email;
import com.soodora.domain.user.value.Password;
import com.soodora.domain.user.value.Username;
import com.soodora.service.auth.UserRegisterCommand;

import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import static java.util.Collections.emptySet;

@Component
@RequiredArgsConstructor
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public User registerUser(UserRegisterCommand command) {
        Password password = new Password(command.password());

        return new User(
                null,
                new Username(command.username()),
                new Email(command.email()),
                passwordEncoder.encode(password.value()),
                command.firstname(),
                command.lastname(),
                new Date(),
                new Date(),
                "",
                null,
                emptySet(),
                emptySet(),
                emptySet(),
                false);
    }

}
