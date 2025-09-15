package com.soodora.infrastructure.auth;

import com.soodora.domain.auth.RoleDTO;
import com.soodora.infrastructure.common.DomainEntity;
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
