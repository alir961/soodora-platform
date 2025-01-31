package com.grooveconductor.infrastructure.playlist;

import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.playlist.PlaylistPrivacy;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class PlaylistEntity implements DomainEntity<PlaylistDTO> {

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
    private PlaylistPrivacy privacy;

    @Column(length = 5000)
    private String imageUrl;

    @Column
    private int numberOfTracks;

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
    public PlaylistDTO toDTO() {
        return new PlaylistDTO(
                slug,
                name,
                description,
                numberOfTracks,
                privacy,
                imageUrl,
                user.toDTO());
    }

}
