package com.soodora.infrastructure.auth;

import com.soodora.domain.auth.RoleRepository;
import com.soodora.domain.auth.value.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryTest {

    private static final String ARTIST_ROLE = "ARTIST";

    @Mock
    private RoleJpaRepository roleJpaRepository;

    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository = new RoleRepositoryImpl(roleJpaRepository);
    }

    @Test
    void givenRoleName_whenCreateRole_thenRoleIsCreated() {
        // WHEN
        roleRepository.createRole(ARTIST_ROLE);

        // THEN
        verify(roleJpaRepository).save(any(RoleEntity.class));
    }

    @Test
    void givenRoleName_whenFindByName_thenRoleIsReturned() {
        // GIVEN
        RoleEntity roleEntity = new RoleEntity();

        roleEntity.setName(ARTIST_ROLE);
        when(roleJpaRepository.findByName(ARTIST_ROLE)).thenReturn(roleEntity);

        // WHEN
        Role result = roleRepository.findByName(ARTIST_ROLE);

        // THEN
        assertNotNull(result);
        assertEquals(ARTIST_ROLE, result.value());
    }

    @Test
    void givenNonExistentRoleName_whenFindByName_thenNullIsReturned() {
        // GIVEN
        String unknownRoleName = "UNKNOWN";

        when(roleJpaRepository.findByName(unknownRoleName)).thenReturn(null);

        // WHEN
        Role result = roleRepository.findByName(unknownRoleName);

        // THEN
        assertNull(result);
    }
}
