package com.soodora.infrastructure.auth;

import com.soodora.domain.auth.RoleDTO;
import com.soodora.domain.auth.RoleRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleJpaRepository roleRepository;

    @Override
    @Transactional
    public void createRole(String name) {
        RoleEntity role = new RoleEntity();

        role.setName(name);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public RoleDTO findByName(String name) {
        RoleEntity role = roleRepository.findByName(name);

        if (role == null)
            return null;
        return role.toDTO();
    }
}
