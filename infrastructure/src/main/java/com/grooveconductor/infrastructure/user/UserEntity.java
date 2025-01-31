package com.grooveconductor.infrastructure.user;

import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.artist.ArtistEntity;
import com.grooveconductor.infrastructure.auth.RoleEntity;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.playlist.PlaylistEntity;
import com.grooveconductor.infrastructure.product.ProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;

@Getter
@Setter
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
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

    @JoinColumn(unique = true)
    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private ArtistEntity artist;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private List<PlaylistEntity> playlists;

    @ManyToMany(fetch = EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<RoleEntity> roles;

    @Override
    public UserDTO toDTO() {
        Set<String> rolesNames = roles.stream()
                .map(RoleEntity::getName)
                .map(name -> name.replaceFirst("ROLE_", ""))
                .collect(toSet());

        return new UserDTO(
                slug,
                username,
                email,
                firstname,
                lastname,
                rolesNames);
    }

}
