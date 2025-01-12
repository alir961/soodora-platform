package com.grooveconductor.service.user;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.user.*;
import com.grooveconductor.service.artist.ArtistException;
import com.grooveconductor.service.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public List<UserDTO> retrieveAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO retrieveUser(Identifier identifier) throws UnknownIdentifierException {
        try {
            return userRepository.find(identifier);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public boolean existsUser(Identifier identifier) {
        return userRepository.isUsed(identifier);
    }

    public UserDTO updateUser(UpdateUserCommand command, String slug) {
        try {
            return userRepository.update(command, new Identifier(Identifier.IdentifierType.SLUG, slug));
        } catch (UnknownSlugException exception) {
            throw new UserUnityException(exception.getMessage());
        }
    }

    public void deleteUser(Identifier identifier) throws UnknownUserException {
        try {
            userRepository.delete(identifier);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public boolean isArtist(Identifier identifier) {
        return userRepository.retrieveArtist(identifier).isPresent();
    }

    public ArtistDTO findArtist(Identifier identifier) {
        return userRepository.retrieveArtist(identifier)
                .orElseThrow(() -> new ArtistException("Must be an artist to do this action"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = retrieveUser(createFromEmail(username));

        log.info(user.toString());
        return new UserPrincipal(user);
    }

}
