package com.grooveconductor.application.user;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UnknownIdentifierException;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.application.artist.ArtistException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public List<UserDTO> retrieveAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO retrieveUser(Identifier identifier) {
        try {
            return userRepository.find(identifier);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
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

        return new User(user.email(), null, emptyList());
    }

}
