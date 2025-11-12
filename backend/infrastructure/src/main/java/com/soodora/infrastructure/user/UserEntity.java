package com.soodora.infrastructure.user;

import com.soodora.domain.auth.value.Role;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.AvatarUrl;
import com.soodora.domain.user.value.Email;
import com.soodora.domain.user.value.Username;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.auth.RoleEntity;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.playlist.PlaylistEntity;
import com.soodora.infrastructure.post.CommentEntity;
import com.soodora.infrastructure.post.LikeEntity;

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
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

@Getter
@Setter
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements DomainEntityAdapter<User> {

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
    private List<PlaylistEntity> playlists;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private List<LikeEntity> likes;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = ALL)
    private List<CommentEntity> comments;

    @ManyToMany(fetch = EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<RoleEntity> roles;

    @ManyToMany
    @JoinTable(name = "users_followers", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id"))
    private Set<UserEntity> followers;

    @ManyToMany
    @JoinTable(name = "users_followers", joinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<UserEntity> following;

    public boolean isArtist() {
        if (artist == null)
            return false;
        return roles.stream().anyMatch(RoleEntity::isArtistRole);
    }

    @Override
    public User aggregate() {
        Set<Role> rolesAggregates = roles.stream()
                .map(DomainEntityAdapter::aggregate)
                .collect(toSet());

        return new User(
                slug,
                new Username(username),
                new Email(email),
                password,
                firstname,
                lastname,
                createdAt,
                updatedAt,
                description,
                avatarUrl == null ? null : new AvatarUrl(avatarUrl),
                rolesAggregates,
                emptySet(),
                emptySet());
    }

    public User aggregateFollowers() {
        Set<Role> rolesAggregates = roles.stream()
                .map(DomainEntityAdapter::aggregate)
                .collect(toSet());
        Set<User> followersAggregates = followers.stream()
                .map(DomainEntityAdapter::aggregate)
                .collect(toSet());
        Set<User> followingAggregates = following.stream()
                .map(DomainEntityAdapter::aggregate)
                .collect(toSet());

        return new User(
                slug,
                new Username(username),
                new Email(email),
                password,
                firstname,
                lastname,
                createdAt,
                updatedAt,
                description,
                avatarUrl == null ? null : new AvatarUrl(avatarUrl),
                rolesAggregates,
                followersAggregates,
                followingAggregates);
    }

}
