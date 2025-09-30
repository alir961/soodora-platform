package com.soodora.infrastructure.user;

import com.soodora.domain.auth.RegisterCommand;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UpdateUserCommand;
import com.soodora.domain.user.UserDTO;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    private static final RegisterCommand VALID_REGISTER = new RegisterCommand(
            "username",
            "user@example.com",
            "password",
            "firstname",
            "lastname",
            "external");

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private UserEntity validUser;

    @BeforeEach
    void setUp() {
        validUser = UserEntityFactory.buildDefaultUserEntity();
    }

    @Test
    void givenValidRegisterCommand_whenSave_thenUserIsSavedAndReturned() {
        // GIVEN
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(validUser);

        // WHEN
        UserDTO result = userRepository.save(VALID_REGISTER);

        // THEN
        assertNotNull(result);
        assertEquals(validUser.getSlug(), result.slug());
        verify(userJpaRepository).save(any(UserEntity.class));
    }

    @Test
    void givenValidUpdateCommand_whenUpdate_thenUserIsUpdated() {
        // GIVEN
        UpdateUserCommand command = new UpdateUserCommand("newUsername", null, "newLastname", "");
        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");

        when(userJpaRepository.findBySlug(validUser.getSlug())).thenReturn(Optional.of(validUser));
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(validUser);

        // WHEN
        UserDTO result = userRepository.update(command, createFromSlug(validUser.getSlug()), eq(fileData));

        // THEN
        assertNotNull(result);
        assertEquals("newUsername", result.username());
        assertEquals("newLastname", result.lastname());
        verify(userJpaRepository).save(validUser);
    }

    @Test
    void givenValidIdentifier_whenFind_thenUserIsReturned() {
        // GIVEN
        Identifier identifier = createFromSlug(validUser.getSlug());

        when(userJpaRepository.findBySlug(validUser.getSlug())).thenReturn(Optional.of(validUser));

        // WHEN
        UserDTO result = userRepository.find(identifier);

        // THEN
        assertNotNull(result);
        assertEquals(validUser.getSlug(), result.slug());
    }

    @Test
    void givenIdentifier_whenDelete_thenUserIsDeleted() {
        // GIVEN
        Identifier identifier = createFromSlug(validUser.getSlug());

        when(userJpaRepository.findBySlug(validUser.getSlug())).thenReturn(Optional.of(validUser));

        // WHEN
        userRepository.delete(identifier);

        // THEN
        verify(userJpaRepository).deleteById(validUser.getId());
    }

    @Test
    void givenIdentifier_whenIsUsed_thenReturnTrue() {
        // GIVEN
        Identifier identifier = createFromSlug(validUser.getSlug());

        when(userJpaRepository.findBySlug(validUser.getSlug())).thenReturn(Optional.of(validUser));

        // WHEN
        boolean result = userRepository.isUsed(identifier);

        // THEN
        assertTrue(result);
    }

}
