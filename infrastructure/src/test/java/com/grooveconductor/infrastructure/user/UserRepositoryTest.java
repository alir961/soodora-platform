package com.grooveconductor.infrastructure.user;

import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UpdateUserCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.artist.ArtistJpaRepository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;
import static com.grooveconductor.infrastructure.product.ProductEntityFactory.buildDefaultProductEntity;

class UserRepositoryImplTest {

    private static final RegisterCommand VALID_REGISTER = new RegisterCommand(
            "username",
            "user@example.com",
            "password",
            "firstname",
            "lastname",
            "external");

    private UserJpaRepository userJpaRepository;
    private UserRepositoryImpl userRepository;
    private UserEntity validUser;

    @BeforeEach
    void setUp() {
        userJpaRepository = mock(UserJpaRepository.class);
        ArtistJpaRepository artistJpaRepository = mock(ArtistJpaRepository.class);
        userRepository = new UserRepositoryImpl(userJpaRepository, artistJpaRepository);
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
        UpdateUserCommand command = new UpdateUserCommand("newUsername", null, "newLastname", null);

        when(userJpaRepository.findBySlug(validUser.getSlug())).thenReturn(Optional.of(validUser));
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(validUser);

        // WHEN
        UserDTO result = userRepository.update(command, createFromSlug(validUser.getSlug()));

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

    @Test
    void givenIdentifier_whenRetrieveProducts_thenReturnProducts() {
        // GIVEN
        Identifier identifier = createFromSlug(validUser.getSlug());

        validUser.setProducts(singletonList(buildDefaultProductEntity(validUser)));
        when(userJpaRepository.findBySlug(validUser.getSlug())).thenReturn(Optional.of(validUser));

        // WHEN
        List<ProductDTO> result = userRepository.retrieveProducts(identifier);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
    }

}
