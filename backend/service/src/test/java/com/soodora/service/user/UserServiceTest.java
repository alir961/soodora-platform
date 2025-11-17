package com.soodora.service.user;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.user.*;
import com.soodora.domain.user.value.Identifier;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static com.soodora.domain.user.value.Identifier.Factory.createFromEmail;
import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;
import static com.soodora.test.TestConstants.ARTIST_USER;
import static com.soodora.test.TestConstants.USER;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageStorageUploader imageStorageUploader;

    @InjectMocks
    private UserService userService;

    private static final Set<User> USERS = Set.of(USER);
    private static final Identifier IDENTIFIER = createFromSlug(USER.getSlug());

    @Test
    void retrieveAllUsers_Success() {
        // GIVEN
        when(userRepository.findAll()).thenReturn(USERS);

        // WHEN
        Set<User> retrievedUsers = userService.retrieveAllUsers();

        // THEN
        assertEquals(USERS, retrievedUsers);
    }

    @Test
    void retrieveAllUsers_NoContent() {
        // GIVEN
        when(userRepository.findAll()).thenReturn(emptySet());

        // WHEN
        Set<User> retrievedUsers = userService.retrieveAllUsers();

        // THEN
        assertTrue(retrievedUsers.isEmpty());
    }

    @Test
    void retrieveUserBySlug_Success() {
        // GIVEN
        when(userRepository.find(IDENTIFIER)).thenReturn(USER);

        // WHEN
        User retrievedUser = userService.retrieveUser(IDENTIFIER);

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
        Identifier identifier = createFromEmail(USER.getEmail().value());

        when(userRepository.find(identifier)).thenReturn(USER);

        // WHEN
        User retrievedUser = userService.retrieveUser(identifier);

        // THEN
        assertEquals(USER, retrievedUser);
    }

    @Test
    void updateUser_Success() {
        // GIVEN
        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");
        UserUpdateCommand updateCommand = new UserUpdateCommand("username", "John", "Doe",
                "Description", fileData);

        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        when(userRepository.save(any(User.class))).thenReturn(USER);
        when(imageStorageUploader.upload(fileData)).thenReturn("https://cdn.soodora.com/image.jpg");

        // WHEN
        User updatedUser = userService.updateUser(USER.getIdentifier(), updateCommand);

        // THEN
        assertEquals(USER, updatedUser);
        verify(userRepository).save(updatedUser);
    }

    @Test
    void updateUser_UnknownIdentifier() {
        // GIVEN
        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");
        UserUpdateCommand updateCommand = new UserUpdateCommand("New Name", "Name", "lastName",
                "Description", fileData);

        when(userRepository.find(USER.getIdentifier()))
                .thenThrow(new UnknownIdentifierException("Identifier not found"));

        // WHEN
        Executable executable = () -> userService.updateUser(USER.getIdentifier(), updateCommand);

        // THEN
        assertThrows(UnknownUserException.class, executable, "Identifier not found");
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

    @Test
    void searchUsersByUsername_OnlyArtists_Success() {
        // GIVEN
        boolean onlyArtists = true;
        Pageable pageable = PageRequest.of(0, 20);
        List<User> usersList = List.of(ARTIST_USER);
        Page<User> expectedPage = new PageImpl<>(usersList, pageable, usersList.size());

        when(userRepository.searchUsersByUsername(ARTIST_USER.getUsername().value(), onlyArtists, pageable))
                .thenReturn(expectedPage);

        // WHEN
        Page<User> result = userService.searchUsersByUsername(ARTIST_USER.getUsername().value(), onlyArtists,
                pageable);

        // THEN
        assertEquals(expectedPage, result);
        verify(userRepository).searchUsersByUsername(ARTIST_USER.getUsername().value(), onlyArtists, pageable);
    }

    @Test
    void searchUsersByUsername_AllUsers_Success() {
        // GIVEN
        boolean onlyArtists = false;
        Pageable pageable = PageRequest.of(0, 20);
        List<User> usersList = List.of(USER);
        Page<User> expectedPage = new PageImpl<>(usersList, pageable, usersList.size());

        when(userRepository.searchUsersByUsername(USER.getUsername().value(), onlyArtists, pageable))
                .thenReturn(expectedPage);

        // WHEN
        Page<User> result = userService.searchUsersByUsername(USER.getUsername().value(), onlyArtists, pageable);

        // THEN
        assertEquals(expectedPage, result);
        verify(userRepository).searchUsersByUsername(USER.getUsername().value(), onlyArtists, pageable);
    }

}
