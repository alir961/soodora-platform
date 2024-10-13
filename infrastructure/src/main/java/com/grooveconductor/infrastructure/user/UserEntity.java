package com.grooveconductor.infrastructure.user;

import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.artist.ArtistEntity;
import com.grooveconductor.infrastructure.common.DomainEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static java.util.UUID.randomUUID;

@Getter
@Setter
@Entity(name = "users")
@NoArgsConstructor
public class UserEntity implements DomainEntity<UserDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = randomUUID();

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String firstname;

    @Column
    private String lastname;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    @JoinColumn(unique = true)
    private ArtistEntity artist;

    @Override
    public UserDTO toDTO() {
        return new UserDTO(slug, username, email, firstname, lastname);
    }

}
