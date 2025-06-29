package com.soodora.infrastructure.user;

import com.soodora.domain.user.UserDTO;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.auth.RoleEntity;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.linkpage.LinkPageEntity;
import com.soodora.infrastructure.playlist.PlaylistEntity;
import com.soodora.infrastructure.post.CommentEntity;
import com.soodora.infrastructure.post.LikeEntity;
import com.soodora.infrastructure.post.PostEntity;
import com.soodora.infrastructure.product.ProductEntity;

import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static java.util.stream.Collectors.toSet;

@Getter
@Setter
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements DomainEntity<UserDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;

    @Column(nullable = false)
    String description;

    @Column
    private String avatarUrl;

    @JoinColumn(unique = true)
    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private ArtistEntity artist;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private List<PlaylistEntity> playlists;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private List<PostEntity> posts;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private List<LikeEntity> likes;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private List<CommentEntity> comments;

    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private LinkPageEntity linkPage;

    @ManyToMany(fetch = EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<RoleEntity> roles;

    @ManyToMany
    @JoinTable(name = "users_followers", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id"))
    private Set<UserEntity> followers;

    @ManyToMany
    @JoinTable(name = "users_followers", joinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<UserEntity> following;

    public void follow(UserEntity user) {
        following.add(user);
        user.followers.add(this);
    }

    public void unfollow(UserEntity user) {
        following.remove(user);
        user.followers.remove(this);
    }

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
                description,
                avatarUrl,
                rolesNames);
    }

}
