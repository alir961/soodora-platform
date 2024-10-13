package com.grooveconductor.infrastructure.playlist;

import com.grooveconductor.domain.playlist.PlaylistPrivacy;
import com.grooveconductor.domain.playlist.PlaylistDto;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
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
@Entity(name = "playlists")
@NoArgsConstructor
public class PlaylistEntity implements DomainEntity<PlaylistDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = randomUUID();

    @Column(unique = true)
    private String slug;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private PlaylistPrivacy state;

    @Column(length = 5000)
    private String imageUrl;

    @ManyToOne
    @JoinColumn
    private UserEntity user;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public PlaylistDto toDTO() {
        return new PlaylistDto(slug, name, description, state, imageUrl, user.getSlug());
    }

}
