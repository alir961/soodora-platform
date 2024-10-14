package com.grooveconductor.infrastructure.artist;

import com.grooveconductor.domain.artist.ArtistDTO;
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
@Entity(name = "artists")
@NoArgsConstructor
public class ArtistEntity implements DomainEntity<ArtistDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = randomUUID();

    @Column(unique = true)
    private String slug;

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private String imageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @JoinColumn
    @OneToOne
    private UserEntity user;

    @Override
    public ArtistDTO toDTO() {
        return new ArtistDTO(
            slug,
            name,
            description,
            imageUrl
        );
    }

}
