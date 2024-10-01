package com.grooveconductor.service;

import com.grooveconductor.domain.dto.ArtistDTO;
import com.grooveconductor.domain.dto.Identifier;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.exception.UnknownIdentifierException;
import com.grooveconductor.domain.repository.UserRepository;
import com.grooveconductor.exception.ArtistException;
import com.grooveconductor.exception.UnknownUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.grooveconductor.domain.factory.IdentifierFactory.createFromEmail;
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
