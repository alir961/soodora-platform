package com.soodora.service.auth;

import com.soodora.domain.auth.AuthenticationRepository;
import com.soodora.domain.auth.RegisterConfirmationRepository;
import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.mail.ConfirmationEmailSender;
import com.soodora.domain.oauth.AuthorizationMapper;
import com.soodora.domain.oauth.OAuthProvider;
import com.soodora.domain.oauth.OAuthTokenRepository;
import com.soodora.domain.oauth.RefreshableAuthorization;
import com.soodora.domain.playlist.CreatePlaylistCommand;
import com.soodora.domain.playlist.PlaylistRepository;
import com.soodora.domain.user.*;
import com.soodora.domain.user.value.Identifier;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserFactory;
import com.soodora.service.user.UserUnityException;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.oauth.OAuthProvider.GOOGLE;
import static com.soodora.domain.playlist.PlaylistPrivacy.PRIVATE;
import static com.soodora.domain.user.value.Identifier.Factory.createFromEmail;
import static com.soodora.domain.user.value.Identifier.Factory.createFromUsername;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final OAuthTokenRepository oauthTokenRepository;
    private final RegisterConfirmationRepository registerConfirmationRepository;
    private final PlaylistRepository playlistRepository;

    private final UserFactory userFactory;
    private final AuthorizationMapper authorizationMapper;
    private final ConfirmationEmailSender emailSender;

    @Value("${application.url:localhost}")
    private String frontUrl;

    @Value("${playlist.liked.title}")
    private String likedPlaylistName;

    public static final String LIKED_AUDIO_PLAYLIST_DESCRIPTION = "All my favorite audios in one playlist!";

    public void registerUser(UserRegisterCommand command) {
        User user = userFactory.registerUser(command);
        String secret = generateSlug(16);

        if (userRepository.isUsed(createFromEmail(command.email())))
            throw new UserUnityException("Email is already used");
        if (userRepository.isUsed(createFromUsername(command.username())))
            throw new UserUnityException("Username is already used");
        while (registerConfirmationRepository.hasConfirmation(secret))
            secret = generateSlug(16);
        registerConfirmationRepository.registerConfirmation(secret, user, command.external());
        emailSender.sendEmail(command.email(), command.username(), frontUrl + "/confirm?secret=" + secret);
    }

    public void confirmUserRegistration(String secret) {
        if (!registerConfirmationRepository.hasConfirmation(secret))
            throw new AuthenticationException("Unable to confirm a registration for that secret");
        Map.Entry<User, String> entry = registerConfirmationRepository.popConfirmation(secret);
        User user = entry.getKey();
        String external = entry.getValue();

        user = userRepository.save(user);

        playlistRepository.save(
                new CreatePlaylistCommand(likedPlaylistName, LIKED_AUDIO_PLAYLIST_DESCRIPTION, PRIVATE),
                user, null);
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
            User user = userRepository.find(identifier);
            String password = authenticationRepository.retrieveEncodedPassword(identifier);

            if (!passwordEncoder.matches(command.password(), password))
                throw new UnknownUserException("Invalid credentials");
            return tokenRepository.generateToken(user);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException("Invalid credentials");
        }
    }

    public List<OAuthProvider> retrieveUserProviders(User user) {
        return oauthTokenRepository.retrieveProviders(user);
    }

}
