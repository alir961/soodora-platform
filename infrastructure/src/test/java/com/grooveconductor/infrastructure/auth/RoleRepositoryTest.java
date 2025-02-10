package com.grooveconductor.infrastructure.auth;

import com.grooveconductor.domain.auth.RoleDTO;
import com.grooveconductor.domain.auth.RoleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleRepositoryTest {

    private static final String ROLE_NAME = "ADMIN";

    private RoleJpaRepository roleJpaRepository;
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleJpaRepository = mock(RoleJpaRepository.class);
        roleRepository = new RoleRepositoryImpl(roleJpaRepository);
    }

    @Test
    void givenRoleName_whenCreateRole_thenRoleIsCreated() {
        // WHEN
        roleRepository.createRole(ROLE_NAME);

        // THEN
        verify(roleJpaRepository).save(any(RoleEntity.class));
    }

    @Test
    void givenRoleName_whenFindByName_thenRoleIsReturned() {
        // GIVEN
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(ROLE_NAME);

        when(roleJpaRepository.findByName(ROLE_NAME)).thenReturn(roleEntity);

        // WHEN
        RoleDTO result = roleRepository.findByName(ROLE_NAME);

        // THEN
        assertNotNull(result);
        assertEquals(ROLE_NAME, result.name());
    }

    @Test
    void givenNonExistentRoleName_whenFindByName_thenNullIsReturned() {
        // GIVEN
        String unknownRoleName = "UNKNOWN";

        when(roleJpaRepository.findByName(unknownRoleName)).thenReturn(null);

        // WHEN
        RoleDTO result = roleRepository.findByName(unknownRoleName);

        // THEN
        assertNull(result);
    }
}
