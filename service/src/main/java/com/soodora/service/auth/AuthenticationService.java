package com.soodora.service.auth;

import com.soodora.domain.auth.LoginCommand;
import com.soodora.domain.auth.RegisterCommand;
import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.oauth.AuthorizationMapper;
import com.soodora.domain.oauth.OAuthProvider;
import com.soodora.domain.oauth.OAuthTokenRepository;
import com.soodora.domain.oauth.RefreshableAuthorization;
import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserUnityException;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.oauth.OAuthProvider.GOOGLE;
import static com.soodora.domain.user.IdentifierFactory.createFromEmail;
import static com.soodora.domain.user.IdentifierFactory.createFromUsername;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final OAuthTokenRepository oauthTokenRepository;

    private final AuthorizationMapper authorizationMapper;

    public UserDTO registerUser(RegisterCommand command) {
        String encodedPassword = passwordEncoder.encode(command.password());
        String external = command.external();

        if (userRepository.isUsed(createFromEmail(command.email())))
            throw new UserUnityException("Email is already used");
        if (userRepository.isUsed(createFromUsername(command.username())))
            throw new UserUnityException("Username is already used");
        UserDTO user = userRepository.save(command.updatePassword(encodedPassword));

        if (external != null) {
            RefreshableAuthorization authorization = authorizationMapper.retrieveAuthorization(external);
            if (authorization == null)
                return user;
            String externalId = tokenRepository.getSubjectFromGoogleToken(authorization.externalToken());

            if (oauthTokenRepository.hasAuthorization(GOOGLE, user))
                oauthTokenRepository.deleteAuthorization(GOOGLE, user);
            oauthTokenRepository.createAuthorization(GOOGLE, user, externalId, authorization.refreshToken());
        }
        return user;
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
