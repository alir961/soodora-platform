package com.grooveconductor.api.configuration;

import com.grooveconductor.domain.auth.RoleDTO;
import com.grooveconductor.domain.auth.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialDataConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;
        createRoleIfNotFound("ROLE_PREMIUM");
        createRoleIfNotFound("ROLE_ARTIST");
        createRoleIfNotFound("ROLE_ADMIN");
        alreadySetup = true;
    }

    @Transactional
    void createRoleIfNotFound(String name) {
        RoleDTO role = roleRepository.findByName(name);
        if (role == null) roleRepository.createRole(name);
    }

}
