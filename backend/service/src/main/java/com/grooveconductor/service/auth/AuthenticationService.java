package com.grooveconductor.service.auth;

import com.grooveconductor.domain.auth.LoginCommand;
import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.auth.TokenRepository;
import com.grooveconductor.domain.oauth.OAuthProvider;
import com.grooveconductor.domain.oauth.OAuthTokenRepository;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.user.UnknownUserException;
import com.grooveconductor.service.user.UserUnityException;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final OAuthTokenRepository oauthTokenRepository;

    public UserDTO registerUser(RegisterCommand command) {
        String encodedPassword = passwordEncoder.encode(command.password());

        if (userRepository.isUsed(createFromEmail(command.email())))
            throw new UserUnityException("Email is already used");
        return userRepository.save(command.updatePassword(encodedPassword));
    }

    public String loginUser(LoginCommand command) {
        Identifier identifier = createFromEmail(command.email());
        UserDTO user = userRepository.find(identifier);
        String password = userRepository.retrieveEncodedPassword(identifier);

        if (!passwordEncoder.matches(command.password(), password))
            throw new UnknownUserException("Invalid password");
        return tokenRepository.generateToken(user);
    }

    public List<OAuthProvider> retrieveUserProviders(UserDTO user) {
        return oauthTokenRepository.retrieveProviders(user);
    }

}
