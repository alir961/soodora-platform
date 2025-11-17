package com.soodora.infrastructure.auth;

import com.soodora.domain.auth.value.Role;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.user.UserEntity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "roles")
public class RoleEntity implements DomainEntityAdapter<Role> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;

    @Override
    public Role aggregate() {
        return new Role(name);
    }

    public boolean isArtistRole() {
        return name.equals("ROLE_ARTIST") || name.equals("ROLE_PREMIUM");
    }

}
