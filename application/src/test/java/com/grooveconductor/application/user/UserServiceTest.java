package com.grooveconductor.application.user;

import com.grooveconductor.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.grooveconductor.domain.user.Identifier.IdentifierType.EMAIL;
import static com.grooveconductor.domain.user.Identifier.IdentifierType.SLUG;
import static com.grooveconductor.test.TestConstants.USER;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static java.util.Optional.of;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

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
    void updateUser_Success() {
        // GIVEN
        UpdateUserCommand updateCommand = new UpdateUserCommand(
            of("New Name"),
            of("Name"),
            of("lastName"));
        Identifier identifier = new Identifier(SLUG, USER.slug());

        when(userRepository.update(updateCommand, identifier)).thenReturn(USER);

        // WHEN
        UserDTO updatedUser = userService.updateUser(updateCommand, USER.slug());

        // THEN
        assertEquals(USER, updatedUser);
        verify(userRepository, times(1)).update(updateCommand, identifier);
    }

    @Test
    void updateUser_UnknownIdentifier() {
        // GIVEN
        UpdateUserCommand updateCommand = new UpdateUserCommand(
            of("New Name"),
            of("Name"),
            of("lastName"));
        Identifier identifier = new Identifier(SLUG, USER.slug());

        when(userRepository.update(updateCommand, identifier)).thenThrow(new UnknownIdentifierException("Identifier not found"));

        // WHEN
        Executable executable = () -> userService.updateUser(updateCommand, USER.slug());

        // THEN
        assertThrows(UnknownIdentifierException.class, executable);
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