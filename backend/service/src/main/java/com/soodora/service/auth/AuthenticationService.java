package com.soodora.service.auth;

import com.soodora.domain.auth.LoginCommand;
import com.soodora.domain.auth.RegisterCommand;
import com.soodora.domain.auth.RegisterConfirmationRepository;
import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.mail.ConfirmationEmailSender;
import com.soodora.domain.oauth.AuthorizationMapper;
import com.soodora.domain.oauth.OAuthProvider;
import com.soodora.domain.oauth.OAuthTokenRepository;
import com.soodora.domain.oauth.RefreshableAuthorization;
import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UnknownIdentifierException;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserUnityException;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
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
    private final RegisterConfirmationRepository registerConfirmationRepository;

    private final AuthorizationMapper authorizationMapper;
    private final ConfirmationEmailSender emailSender;

    @Value("${application.url:localhost}")
    private String frontUrl;

    public void registerUser(RegisterCommand command) {
        String encodedPassword = passwordEncoder.encode(command.password());
        String secret = generateSlug(16);

        if (userRepository.isUsed(createFromEmail(command.email())))
            throw new UserUnityException("Email is already used");
        if (userRepository.isUsed(createFromUsername(command.username())))
            throw new UserUnityException("Username is already used");
        while (registerConfirmationRepository.hasConfirmation(secret))
            secret = generateSlug(16);
        registerConfirmationRepository.registerConfirmation(secret, command.updatePassword(encodedPassword));
        emailSender.sendEmail(command.email(), command.username(), frontUrl + "/confirm?secret=" + secret);
    }

    public void confirmUserRegistration(String secret) {
        if (!registerConfirmationRepository.hasConfirmation(secret))
            throw new AuthenticationException("Unable to confirm a registration for that secret");
        RegisterCommand command = registerConfirmationRepository.popConfirmation(secret);
        UserDTO user = userRepository.save(command);
        String external = command.external();

        if (external != null) {
            RefreshableAuthorization authorization = authorizationMapper.retrieveAuthorization(external);
            if (authorization == null)
                return;
            String externalId = tokenRepository.getSubjectFromGoogleToken(authorization.externalToken());

            if (oauthTokenRepository.hasAuthorization(GOOGLE, user))
                oauthTokenRepository.deleteAuthorization(GOOGLE, user);
            oauthTokenRepository.createAuthorization(GOOGLE, user, externalId, authorization.refreshToken());
        }
    }

    public String loginUser(LoginCommand command) {
        try {
            Identifier identifier = command.identifier();
            UserDTO user = userRepository.find(identifier);
            String password = userRepository.retrieveEncodedPassword(identifier);

            if (!passwordEncoder.matches(command.password(), password))
                throw new UnknownUserException("Invalid credentials");
            return tokenRepository.generateToken(user);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException("Invalid credentials");
        }
    }

    public List<OAuthProvider> retrieveUserProviders(UserDTO user) {
        return oauthTokenRepository.retrieveProviders(user);
    }

}
