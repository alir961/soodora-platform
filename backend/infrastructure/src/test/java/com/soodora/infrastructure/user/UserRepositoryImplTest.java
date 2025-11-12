package com.soodora.infrastructure.user;

import com.soodora.domain.auth.value.Role;
import com.soodora.domain.user.UnknownIdentifierException;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Identifier;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.auth.RoleEntity;
import com.soodora.infrastructure.auth.RoleJpaRepository;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;
import static com.soodora.infrastructure.user.UserEntityFactory.buildDefaultUserEntity;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.UserFactory.buildDefaultUser;
import static com.soodora.test.factory.UserFactory.buildNewUser;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    private static final String ARTIST_ROLE = "ROLE_ARTIST";

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private RoleJpaRepository roleJpaRepository;

    @Mock
    private ArtistJpaRepository artistJpaRepository;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = buildDefaultUserEntity();
    }

    @Test
    void givenNewUser_whenSave_thenUserEntityIsCreated() {
        // GIVEN
        User user = buildNewUser();

        when(userJpaRepository.findBySlug(null)).thenReturn(empty());
        when(userJpaRepository.findBySlug(anyString())).thenReturn(empty());
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // WHEN
        User saved = userRepository.save(user);

        // THEN
        assertEquals(user.getEmail(), saved.getEmail());
    }

    @Test
    void givenExistingUser_whenSave_thenUserIsUpdated() {
        // GIVEN
        User user = buildDefaultUser();

        when(userJpaRepository.findBySlug(user.getSlug())).thenReturn(of(userEntity));
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // WHEN
        User updated = userRepository.save(user);

        // THEN
        assertEquals(user.getSlug(), updated.getSlug());
    }

    @Test
    void givenUserWithoutArtistRole_whenSave_thenArtistCreated() {
        // GIVEN
        User user = USER;
        Identifier identifier = createFromSlug(userEntity.getSlug());
        Role artistRole = new Role(ARTIST_ROLE);
        Set<Role> roles = Set.of(artistRole);
        RoleEntity artistRoleEntity = new RoleEntity();

        user.updateRoles(roles);
        artistRoleEntity.setName(ARTIST_ROLE);

        when(userJpaRepository.findBySlug(identifier.value())).thenReturn(of(userEntity));
        when(roleJpaRepository.findByName(artistRole.completeRole())).thenReturn(artistRoleEntity);
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // WHEN
        userRepository.save(user);

        // THEN
        verify(artistJpaRepository).save(any(ArtistEntity.class));
        verify(userJpaRepository).save(any(UserEntity.class));
    }

    @Test
    void givenUserWithArtistRole_whenSave_thenArtistDeleted() {
        // GIVEN
        User user = USER;
        Identifier identifier = createFromSlug(userEntity.getSlug());
        RoleEntity artistRole = new RoleEntity();
        ArtistEntity artistEntity = new ArtistEntity();

        user.updateRoles(emptySet());

        artistRole.setId(1L);
        artistRole.setName(ARTIST_ROLE);
        userEntity.setRoles(Set.of(artistRole));
        userEntity.setArtist(artistEntity);

        when(userJpaRepository.findBySlug(identifier.value())).thenReturn(of(userEntity));
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // WHEN
        userRepository.save(user);

        // THEN
        verify(artistJpaRepository).delete(artistEntity);
    }

    @Test
    void givenDefaultUser_whenFindAll_thenReturnUsers() {
        // GIVEN
        User user = buildDefaultUser();

        when(userJpaRepository.findAll()).thenReturn(singletonList(userEntity));

        // WHEN
        Set<User> users = userRepository.findAll();

        // THEN
        User firstUser = users.iterator().next();

        assertEquals(1, users.size());
        assertEquals(user.getSlug(), firstUser.getSlug());
    }

    @Test
    void givenValidIdentifier_whenFind_thenReturnUser() {
        // GIVEN
        Identifier identifier = createFromSlug(userEntity.getSlug());

        when(userJpaRepository.findBySlug(identifier.value())).thenReturn(of(userEntity));

        // WHEN
        User result = userRepository.find(identifier);

        // THEN
        assertEquals(userEntity.getSlug(), result.getSlug());
    }

    @Test
    void givenUnknownIdentifier_whenFind_thenThrowException() {
        // GIVEN
        Identifier identifier = createFromSlug("unknown");

        when(userJpaRepository.findBySlug(identifier.value())).thenReturn(empty());

        // WHEN
        Executable executable = () -> userRepository.find(identifier);

        // THEN
        assertThrows(UnknownIdentifierException.class, executable);
    }

    @Test
    void givenExistingIdentifier_whenIsUsed_thenReturnTrue() {
        // GIVEN
        Identifier identifier = createFromSlug(userEntity.getSlug());

        when(userJpaRepository.findBySlug(identifier.value())).thenReturn(of(userEntity));

        // WHEN
        boolean used = userRepository.isUsed(identifier);

        // THEN
        assertTrue(used);
    }

    @Test
    void givenUnknownIdentifier_whenIsUsed_thenReturnFalse() {
        // GIVEN
        Identifier identifier = createFromSlug(userEntity.getSlug());

        when(userJpaRepository.findBySlug(identifier.value())).thenReturn(empty());

        // WHEN
        boolean used = userRepository.isUsed(identifier);

        // THEN
        assertFalse(used);
    }

    @Test
    void givenUsernameQuery_whenSearchUsers_thenReturnUsersPage() {
        // GIVEN
        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<UserEntity> page = new PageImpl<>(singletonList(userEntity));
        String query = "u";

        when(userJpaRepository.findByUsernameContainingIgnoreCase(query, pageable))
                .thenReturn(page);

        // WHEN
        Page<User> result = userRepository.searchUsersByUsername(query, false, pageable);

        // THEN
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void givenValidIdentifier_whenDelete_thenRepositoryCalled() {
        // GIVEN
        Identifier identifier = createFromSlug(userEntity.getSlug());

        when(userJpaRepository.findBySlug(identifier.value())).thenReturn(of(userEntity));

        // WHEN
        userRepository.delete(identifier);

        // THEN
        verify(userJpaRepository).deleteById(userEntity.getId());
    }

    @Test
    void givenIdentifier_whenDeleteAvatar_thenAvatarIsRemoved() {
        // GIVEN
        Identifier identifier = createFromSlug(userEntity.getSlug());

        when(userJpaRepository.findBySlug(identifier.value())).thenReturn(of(userEntity));

        // WHEN
        userRepository.deleteAvatar(identifier);

        // THEN
        verify(userJpaRepository).save(userEntity);
        assertNull(userEntity.getAvatarUrl());
    }

}
