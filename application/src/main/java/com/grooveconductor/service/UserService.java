package com.grooveconductor.service;

import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.exception.UnknownEmailException;
import com.grooveconductor.domain.exception.UnknownSlugException;
import com.grooveconductor.domain.repository.UserRepository;
import com.grooveconductor.exception.UnknownUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public List<UserDTO> retrieveAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO retrieveUserBySlug(String slug) {
        try {
            return userRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public UserDTO retrieveUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (UnknownEmailException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public void deleteUserBySlug(String slug) throws UnknownUserException {
        try {
            userRepository.deleteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = retrieveUserByEmail(username);

        return new User(user.email(), null, emptyList());
    }

}
