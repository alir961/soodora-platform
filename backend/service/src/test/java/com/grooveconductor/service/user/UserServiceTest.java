package com.grooveconductor.service.user;

import com.grooveconductor.domain.user.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;
import static com.grooveconductor.test.TestConstants.USER;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    private static final List<UserDTO> USERS = List.of(USER);
    private static final Identifier IDENTIFIER = createFromSlug(USER.slug());

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
        when(userRepository.find(IDENTIFIER)).thenReturn(USER);

        // WHEN
        UserDTO retrievedUser = userService.retrieveUser(IDENTIFIER);

        // THEN
        assertEquals(USER, retrievedUser);
    }

    @Test
    void retrieveUserBySlug_Unknown() {
        // GIVEN
        Identifier identifier = createFromSlug("unknown");

        when(userRepository.find(any())).thenThrow(UnknownIdentifierException.class);

        // WHEN
        Executable executable = () -> userService.retrieveUser(identifier);

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

    @Test
    void retrieveUserByEmail_Success() {
        // GIVEN
        Identifier identifier = createFromEmail(USER.email());

        when(userRepository.find(identifier)).thenReturn(USER);

        // WHEN
        UserDTO retrievedUser = userService.retrieveUser(identifier);

        // THEN
        assertEquals(USER, retrievedUser);
    }

    @Test
    void updateUser_Success() {
        // GIVEN
        UpdateUserCommand updateCommand = new UpdateUserCommand("New Name", "Name", "lastName",
                "https//soodora.com/assets/avatar.png");

        when(userRepository.update(updateCommand, IDENTIFIER)).thenReturn(USER);

        // WHEN
        UserDTO updatedUser = userService.updateUser(updateCommand, USER.slug());

        // THEN
        assertEquals(USER, updatedUser);
        verify(userRepository).update(updateCommand, IDENTIFIER);
    }

    @Test
    void updateUser_UnknownIdentifier() {
        // GIVEN
        UpdateUserCommand updateCommand = new UpdateUserCommand("New Name", "Name", "lastName",
                "https//soodora.com/assets/avatar.png");

        when(userRepository.update(updateCommand, IDENTIFIER))
                .thenThrow(new UnknownIdentifierException("Identifier not found"));

        // WHEN
        Executable executable = () -> userService.updateUser(updateCommand, USER.slug());

        // THEN
        assertThrows(UnknownIdentifierException.class, executable);
    }

    @Test
    void deleteUserBySlug_Success() {
        // GIVEN
        doNothing().when(userRepository).delete(IDENTIFIER);

        // WHEN
        Executable executable = () -> userService.deleteUser(IDENTIFIER);

        // THEN
        assertDoesNotThrow(executable);
        verify(userRepository).delete(IDENTIFIER);
    }

    @Test
    void deleteUserBySlug_Unknown() {
        // GIVEN
        doThrow(UnknownIdentifierException.class).when(userRepository).delete(IDENTIFIER);

        // WHEN
        Executable executable = () -> userService.deleteUser(IDENTIFIER);

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

}
