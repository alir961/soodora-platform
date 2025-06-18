package com.grooveconductor.infrastructure.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findBySlug(String slug);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Set<UserEntity> findByUsernameContainingIgnoreCase(String username);

    Set<UserEntity> findByUsernameContainingIgnoreCaseAndRoles_Name(String username, String roleName);

}
