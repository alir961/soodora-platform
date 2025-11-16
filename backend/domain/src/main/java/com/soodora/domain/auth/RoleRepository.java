package com.soodora.domain.auth;

import com.soodora.domain.auth.value.Role;

public interface RoleRepository {

    void createRole(String name);
    Role findByName(String name);

}
