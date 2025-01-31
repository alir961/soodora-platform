package com.grooveconductor.infrastructure.auth;

import com.grooveconductor.domain.auth.RoleDTO;
import com.grooveconductor.domain.auth.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleJpaRepository roleRepository;

    @Override
    public void createRole(String name) {
        RoleEntity role = new RoleEntity();

        role.setName(name);
        roleRepository.save(role);
    }

    @Override
    public RoleDTO findByName(String name) {
        RoleEntity role = roleRepository.findByName(name);

        if (role == null)
            return null;
        return role.toDTO();
    }
}
