package com.grooveconductor.service;

import com.grooveconductor.domain.dto.Identifier;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.exception.UnknownIdentifierException;
import com.grooveconductor.domain.repository.UserRepository;
import com.grooveconductor.exception.UnknownUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.grooveconductor.domain.dto.Identifier.IdentifierType.EMAIL;
import static com.grooveconductor.domain.dto.Identifier.IdentifierType.SLUG;
import static com.grooveconductor.test.UserFactory.buildDefaultUser;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    private static final UserDTO USER = buildDefaultUser();
    private static final List<UserDTO> USERS = List.of(USER);

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void retrieveAllUsers_Success() {
        // GIVEN
        when(userRepository.findAll()).thenReturn(USERS);

        // WHEN
        List<UserDTO> retrievedUsers = userService.retrieveAllUsers();

        // THEN
        assertEquals(USERS, retrievedUsers);
    }

    @Test
    void retrieveAllUsers_NoContent() {
        // GIVEN
        when(userRepository.findAll()).thenReturn(emptyList());

        // WHEN
        List<UserDTO> retrievedUsers = userService.retrieveAllUsers();

        // THEN
        assertTrue(retrievedUsers.isEmpty());
    }

    @Test
    void retrieveUserBySlug_Success() {
        // GIVEN
        Identifier identifier = new Identifier(SLUG, USER.slug());

        when(userRepository.find(identifier)).thenReturn(USER);

        // WHEN
        UserDTO retrievedUser = userService.retrieveUser(identifier);

        // THEN
        assertEquals(USER, retrievedUser);
    }

    @Test
    void retrieveUserBySlug_Unknown() {
        // GIVEN
        Identifier identifier = new Identifier(SLUG, "unknown");

        when(userRepository.find(any())).thenThrow(UnknownIdentifierException.class);

        // WHEN
        Executable executable = () -> userService.retrieveUser(identifier);

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

    @Test
    void retrieveUserByEmail_Success() {
        // GIVEN
        Identifier identifier = new Identifier(EMAIL, USER.email());

        when(userRepository.find(identifier)).thenReturn(USER);

        // WHEN
        UserDTO retrievedUser = userService.retrieveUser(identifier);

        // THEN
        assertEquals(USER, retrievedUser);
    }

    @Test
    void deleteUserBySlug_Success() {
        // GIVEN
        Identifier identifier = new Identifier(SLUG, USER.slug());

        doNothing().when(userRepository).delete(identifier);

        // WHEN
        Executable executable = () -> userService.deleteUser(identifier);

        // THEN
        assertDoesNotThrow(executable);
        verify(userRepository, times(1)).delete(identifier);
    }

    @Test
    void deleteUserBySlug_Unknown() {
        // GIVEN
        Identifier identifier = new Identifier(SLUG, USER.slug());

        doThrow(UnknownIdentifierException.class).when(userRepository).delete(identifier);

        // WHEN
        Executable executable = () -> userService.deleteUser(identifier);

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

}