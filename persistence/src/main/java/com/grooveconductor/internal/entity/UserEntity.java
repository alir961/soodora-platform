package com.grooveconductor.internal.entity;

import com.grooveconductor.domain.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Getter
@Setter
@Entity(name = "users")
@NoArgsConstructor
public class UserEntity implements DomainEntity<UserDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = randomUUID();

    @Column(unique = true)
    private String slug;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = false, nullable = true)
    private String firstname;

    @Column(unique = false, nullable = true)
    private String lastname;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public UserDTO toDTO() {
        return new UserDTO(slug, username, email, firstname, lastname);
    }

}
