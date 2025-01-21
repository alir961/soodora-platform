package com.grooveconductor.domain.auth;

public interface RoleRepository {

    void createRole(String name);
    RoleDTO findByName(String name);

}
