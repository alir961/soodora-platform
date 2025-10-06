package com.soodora.api.configuration;

import com.soodora.domain.auth.RoleDTO;
import com.soodora.domain.auth.RoleRepository;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitialDataConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;
        createRoleIfNotFound("ROLE_ARTIST");
        createRoleIfNotFound("ROLE_PREMIUM");
        createRoleIfNotFound("ROLE_ADMIN");
        alreadySetup = true;
    }

    @Transactional
    void createRoleIfNotFound(String name) {
        RoleDTO role = roleRepository.findByName(name);
        if (role == null)
            roleRepository.createRole(name);
    }

}
