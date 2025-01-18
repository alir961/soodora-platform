package com.grooveconductor.infrastructure.auth;

import com.grooveconductor.domain.auth.RoleDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "roles")
public class RoleEntity implements DomainEntity<RoleDTO> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;

    @Override
    public RoleDTO toDTO() {
        return new RoleDTO(id, name);
    }

}
